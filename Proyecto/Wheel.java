
import java.util.ArrayList;

/**
 * Representa una rueda de la máquina tragamonedas.
 * Cada rueda mantiene su propia lista de símbolos (pueden ser
 * distintos de una rueda a otra) y recuerda cuál de ellos está
 * mostrando actualmente. Todos los símbolos de una rueda comparten
 * la misma posición horizontal (la de la ventana de la rueda), así
 * que solo el símbolo actual debe estar visible a la vez.
 */
public class Wheel
{
    private ArrayList<Symbol> symbols;
    private Rectangle slot;
    private int currentPosition;
    private int xPosition;
    private int yPosition;
    private boolean isLock;
    private boolean visible;
    
    public static final int WIDTH = 60;
    public static final int HEIGHT = 60;
    public static final int SPACING = 90;   // separación entre el x de una rueda y la siguiente
    public static final int BASE_X = 60;    // x de la primera rueda (índice 0)
    public static final int BASE_Y = 80;    // y fija para todas las ruedas
    
    /**
     * Crea una rueda vacía, ubicada en la posición horizontal que le
     * corresponde a su posición pos (1-based: la primera rueda es 1).
     */
    public Wheel(int pos)
    {
        symbols = new ArrayList<Symbol>();
        currentPosition = 0;
        isLock = false;
        visible = false;
        xPosition = BASE_X + (pos - 1) * SPACING;
        yPosition = BASE_Y;

        slot = new Rectangle();
        slot.changeSize(HEIGHT, WIDTH);
        slot.moveHorizontal(xPosition - 70);
        slot.moveVertical(yPosition - 15);
        slot.changeColor("white");
    }
    
    /**
     * Reubica esta rueda (su ventana y todos sus símbolos) en la
     * posición horizontal correspondiente a pos (1-based). Se usa
     * después de cualquier addWheel/delWheel para que todas las ruedas
     * queden parejas y sin huecos visuales.
     */
    void relocate(int pos)
    {
        int newX = BASE_X + (pos - 1) * SPACING;
        int deltaX = newX - xPosition;

        if (deltaX != 0){
        slot.moveHorizontal(deltaX);

        for (Symbol symbol : symbols)
        {
            symbol.moveHorizontal(deltaX);
        }

        xPosition = newX;
        }
    }
    
    /**
     * Avanza steps veces, mostrando el giro paso a paso si la rueda
     * está visible. Los últimos pasos se muestran más lentos que los
     * primeros, para dar sensación de que la rueda frena en vez de
     * detenerse de golpe.
     */
    public void spin(int steps)
    {
        if (symbols.isEmpty())
        {
            return;
        }

        for (int i = 0; i < steps; i++)
        {
            newCurrentSymbol();

        if (visible)
        {
            int remaining = steps - i;
            int pause = (remaining <= 3) ? 250 + (4 - remaining) * 150 : 80;
            Canvas.getCanvas().wait(pause);
        }
        }
    }
    /**
     * Este metodo Bloquea una rueda en su posicion actual, no es posible hacer alguna accion sobre ella
     */
    public void lock(){
        isLock = true;
    }
    
    /**
     * Este metodo Desbloquea una rueda para que se pueda operar
     */
    public void unlock(){
        isLock = false;
    }
    
    /**
     * Verficficador para saber si una rueda es operable o no
     */
    public boolean isLocked(){
        return isLock;
    }
    
    /**
     * Crea una rueda vacía y ubica su ventana en (x,y).
     */
    public Wheel(int x, int y)
    {
        symbols = new ArrayList<Symbol>();
        currentPosition = 0;
        xPosition = x;
        yPosition = y;

        slot = new Rectangle();
        slot.moveHorizontal(xPosition - 70);
        slot.moveVertical(yPosition - 15);
    }

    /**
     * Intercambia el contenido lógico de esta rueda con el de other:
     * la lista de símbolos y cuál de ellos está mostrando actualmente.
     * La posición física de cada rueda (su Rectangle) no cambia — solo
     * cambia lo que se ve dentro de cada una.
     */
    public void swapContent(Wheel other){
        ArrayList<Symbol> tempSymbols = this.symbols;
        int tempPosition = this.currentPosition;
    
        this.symbols = other.symbols;
        this.currentPosition = other.currentPosition;
    
        other.symbols = tempSymbols;
        other.currentPosition = tempPosition;
    
        
        for (Symbol symbol : this.symbols){
            symbol.setPosition(this.centeredX(), this.centeredY());
        }

        for (Symbol symbol : other.symbols){
            symbol.setPosition(other.centeredX(), other.centeredY());
        }
    
        
        Symbol myCurrent = currentSymbol();
        if (myCurrent != null){
            this.slot.changeColor(myCurrent.getColor());
        }
    
        Symbol otherCurrent = other.currentSymbol();
        if (otherCurrent != null){
            other.slot.changeColor(otherCurrent.getColor());
        }
    }
    
    /**
     * Agrega un símbolo al final de esta rueda y lo alinea con la
     * ventana de la rueda. Visibilidad de paquete: solo SlotMachine
     * debe decidir qué símbolos tiene cada rueda (por eso no aparece
     * con "+" en el diagrama de clases).
     */
    void addSymbol(Symbol symbol){
        symbol.setPosition(centeredX(), centeredY());
        symbols.add(symbol);
    }

    private int centeredX(){
        return xPosition + (WIDTH - Symbol.DIAMETER) / 2;
    }

    private int centeredY(){
        return yPosition + (HEIGHT - Symbol.DIAMETER) / 2;
    }   

    /**
     * Elimina de esta rueda el primer símbolo cuyo color sea igual a
     * color, si existe, y ajusta la posición actual si queda fuera
     * de rango.
     */
    void delSymbol(String color)
    {
        for (int i = 0; i < symbols.size(); i++)
        {
            if (symbols.get(i).getColor().equals(color))
            {
                symbols.remove(i);

                if (currentPosition >= symbols.size())
                {
                    currentPosition = 0;
                }

                return;
            }
        }
    }

    /**
     * Busca dentro de esta rueda un símbolo del color indicado y, si
     * lo encuentra, lo convierte en el símbolo actual.
     * Devuelve true si lo encontró y lo colocó, false si esta rueda
     * no tiene ningún símbolo de ese color.
     */
    boolean placeSymbol(String color)
    {
        for (int i = 0; i < symbols.size(); i++)
        {
            if (symbols.get(i).getColor().equals(color))
            {
                currentPosition = i;
                slot.changeColor(color);
                return true;
            }
        }

        return false;
    }

    /**
     * Devuelve una copia de la lista de símbolos de esta rueda.
     * Visibilidad de paquete: la usa SlotMachine.symbols() para armar
     * el inventario completo de colores de la máquina. Se devuelve
     * una copia (no la lista real) para no exponer el estado interno.
     */
    ArrayList<Symbol> getSymbols()
    {
        return new ArrayList<Symbol>(symbols);
    }

    /**
     * Símbolo que esta rueda muestra actualmente, o null si la rueda
     * no tiene ningún símbolo.
     */
    public Symbol currentSymbol()
    {
        if (symbols.isEmpty())
        {
            return null;
        }

        return symbols.get(currentPosition);
    }

    /**
     * Avanza al siguiente símbolo de la rueda (cíclico), actualiza el
     * color de la ventana y devuelve el nuevo símbolo actual.
     */
    public Symbol newCurrentSymbol()
    {
        if (symbols.isEmpty())
        {
            return null;
        }

        currentPosition = (currentPosition + 1) % symbols.size();
        Symbol current = symbols.get(currentPosition);
        slot.changeColor(current.getColor());
        return current;
    }

    /**
     * Indica si el símbolo actual de esta rueda tiene el mismo color
     * que el símbolo recibido. Se usa para comparar todas las ruedas
     * entre sí al verificar el jackpot.
     */
    public boolean allSymbolMatch(Symbol currentSymbol){
        Symbol mine = currentSymbol();

        if (mine == null || currentSymbol == null)
        {
            return false;
        }

        return mine.getColor().equals(currentSymbol.getColor());
    }
            
        public void makeVisible(){
        visible = true;
        slot.makeVisible();
        Symbol current = currentSymbol();
    
        if (current != null)
        {
            current.makeVisible();
        }
    }
    
    public void makeInvisible(){
        visible = false;
        slot.makeInvisible();
    
        for (Symbol symbol : symbols)
        {
            symbol.makeInvisible();
        }
    }
}


