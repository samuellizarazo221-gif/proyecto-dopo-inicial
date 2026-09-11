
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
    
    /**
     * Crea una rueda vacía en la posición por defecto (50,50).
     */
    public Wheel()
    {
        this(50, 50);
    }
    
    /**
     * 
     */
    public void lock(){
        isLock = true;
    }
    
    /**
     * 
     */
    public void unlock(){
        isLock = false;
    }
    
    /**
     * 
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
            symbol.setPosition(this.xPosition);
        }
        
        for (Symbol symbol : other.symbols){
            symbol.setPosition(other.xPosition);
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
    void addSymbol(Symbol symbol)
    {
        symbol.setPosition(xPosition);
        symbols.add(symbol);
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
    public boolean allSymbolMatch(Symbol currentSymbol)
    {
        Symbol mine = currentSymbol();

        if (mine == null || currentSymbol == null)
        {
            return false;
        }

        return mine.getColor().equals(currentSymbol.getColor());
    }

    /**
     * Muestra la ventana de la rueda y su símbolo actual. Los demás
     * símbolos de la rueda permanecen ocultos: comparten la misma
     * posición y se verían superpuestos.
     */
    public void makeVisible()
    {
        slot.makeVisible();
        Symbol current = currentSymbol();

        if (current != null)
        {
            current.makeVisible();
        }
    }

    /**
     * Oculta la ventana de la rueda y todos sus símbolos.
     */
    public void makeInvisible()
    {
        slot.makeInvisible();

        for (Symbol symbol : symbols)
        {
            symbol.makeInvisible();
        }
    }
}


