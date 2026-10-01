import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Representa una máquina tragamonedas: administra un conjunto de
 * ruedas, permite girarlas, consultar sus símbolos y verificar si
 * se alcanzó el jackpot.
 */
public class SlotMachine
{
    private static final int minWheels = 3;
    private static final int maxWheels = 50;
    private static final int cabinetMargin = 20;

    private ArrayList<Wheel> wheels;
    private boolean visible;
    private boolean lastOperationOk;
    private Rectangle cabinet;
    private int cabinetX;
    private int cabinetY;
    
    
    /**
     * Crea una máquina sin ruedas, visible por defecto.
     */
    public SlotMachine()
    {
        wheels = new ArrayList<Wheel>();
        visible = true;
        lastOperationOk = true;

        cabinet = new Rectangle();
        cabinetX = 70;
        cabinetY = 15;
        cabinet.changeColor("black");

        for (int pos = 1; pos <= minWheels; pos++)
        {
            addWheel(pos);
        }
    }
    
    /**
     * Solucion de la maquina ciclo 3
     */
    public SlotMachine(int n){
        wheels = new ArrayList<Wheel>();
        visible = true; 
        lastOperationOk = true;
        
        cabinet = new Rectangle();
        cabinetX = 70;
        cabinetY = 15;
        cabinet.changeColor("black");
        
        int size = n;
        if (size < minWheels) { 
            size = minWheels; 
        }
        if (size > maxWheels) { 
            size = maxWheels; 
        }
        
        // Generar size colores hexadecimales distintos, uno por símbolo.
        java.util.HashSet<String> usedColors = new java.util.HashSet<String>();
        java.util.Random random = new java.util.Random();

        while (usedColors.size() < size){
            String color = String.format("#%06x", random.nextInt(0x1000000));
            usedColors.add(color);
        }
        
        String[] palette = usedColors.toArray(new String[0]);
        
        for (int pos = 1; pos <= size; pos++){
            addWheel(pos);
            for (int c = 0; c < size; c++){
                addSymbol(pos, palette[c]);
            }
        }
    
        for (int pos = 1; pos <= size; pos++){
            spin(pos, random.nextInt(size));
        }
    
        if (isJackpot()){
            spin(1, 1);
        }
    
        lastOperationOk = true;
    }
    
    
    /**
     * Inserta una rueda nueva y vacía en la posición pos (1-based).
     * Si pos es menor a 1 se usa la posición 1; si es mayor a la
     * cantidad de ruedas más una, se agrega al final.
     */
    public void addWheel(int pos){
        int clampedPos = clampPosition(pos, wheels.size() + 1);
        int index = clampedPos - 1;

        Wheel wheel = new Wheel(clampedPos);
        wheels.add(index, wheel);
        layoutMachine();

        if (visible)
        {
            wheel.makeVisible();
        }

        lastOperationOk = true;
    }

    /**
     * Elimina la rueda que está en la posición pos.
     */
    public void delWheel(int pos)
    {
        if (pos < 1 || pos > wheels.size())
        {
            error("No existe esa rueda.");
            return;
        }

        int index = pos - 1;
        wheels.get(index).makeInvisible();
        wheels.remove(index);
        layoutMachine();
        lastOperationOk = true;
    }

    /**
     * Crea un símbolo del color indicado y lo agrega a la rueda pos.
     */
    public void addSymbol(int pos, String color)
    {
        if (pos < 0 || pos >= wheels.size())
        {
            error("No existe esa rueda.");
            return;
        }

        Wheel wheel = wheels.get(pos);
        wheel.addSymbol(new Symbol(color));

        if (visible)
        {
            wheel.makeVisible();
        }

        lastOperationOk = true;
    }

    /**
     * Elimina el símbolo del color indicado de todas las ruedas en
     * las que aparezca.
     */
    public void delSymbols(String symbol)
    {
        for (Wheel wheel : wheels)
        {
            wheel.delSymbol(symbol);
        }

        lastOperationOk = true;
    }

    /**
     * Hace que la rueda wheel muestre el símbolo del color indicado,
     * si ya lo tiene entre sus símbolos.
     */
    public void placeSymbol(int wheel, String symbol)
    {
        if (wheel < 1 || wheel >= wheels.size())
        {
            error("No existe esa rueda.");
            return;
        }

        boolean placed = wheels.get(wheel - 1).placeSymbol(symbol);

        if (!placed)
        {
            error("Esa rueda no tiene un símbolo de ese color.");
            return;
        }

        lastOperationOk = true;
    }

    /**
     * Gira una rueda en particular, pasando a su siguiente símbolo.
     */
    public void spin(int wheel)
    {
        if (wheel < 1 || wheel > wheels.size())
        {
            error("No existe esa rueda.");
            return;
        }

        Wheel target = wheels.get(wheel - 1);

        if (target.isLocked())
        {
            error("No se puede girar una rueda bloqueada.");
            return;
        }

        target.newCurrentSymbol();
        lastOperationOk = true;
    }

    /**
     * Gira todas las ruedas de la máquina.
     */
    public void spin(){
        for (Wheel wheel : wheels){
            if (!wheel.isLocked()){
            wheel.newCurrentSymbol();
            }
        }

        lastOperationOk = true;
    }

    public void spin(int wheel, int steps){
        if (wheel < 1 || wheel > wheels.size())
        {
            error("No existe esa rueda.");
            return;
        }

        Wheel target = wheels.get(wheel - 1);

        if (target.isLocked())
        {
            error("No se puede girar una rueda bloqueada.");
            return;
        }

        target.spin(steps);
        lastOperationOk = true;
    }

    public void spin(String[] setSymbols){
        if (setSymbols == null || setSymbols.length != wheels.size())
        {
            error("La cantidad de símbolos no coincide con la cantidad de ruedas.");
            return;
        }

        for (int i = 0; i < setSymbols.length; i++)
        {
            if (!wheels.get(i).placeSymbol(setSymbols[i]))
            {
                error("Una de las ruedas no tiene un símbolo de ese color.");
                return;
            }
        }

        lastOperationOk = true;
    }
    
    /**
     * Colores de todos los símbolos que existen en la máquina (todas
     * las ruedas, todos sus símbolos, no solo el que está mostrando
     * cada una).
     */
    public String[] symbols()
    {
        ArrayList<String> colors = new ArrayList<String>();

        for (Wheel wheel : wheels)
        {
            for (Symbol symbol : wheel.getSymbols())
            {
                colors.add(symbol.getColor());
            }
        }

        lastOperationOk = true;
        return colors.toArray(new String[0]);
    }

    /**
     * Cantidad de colores distintos entre todos los símbolos de la
     * máquina.
     */
    public int distinctSymbols()
    {
        int distinct = Symbol.getDistinctSymbols(symbols());
        lastOperationOk = true;
        return distinct;
    }

    /**
     * Colores actualmente visibles en cada rueda, de izquierda a
     * derecha (una entrada por rueda).
     */
    public String[] configuration()
    {
        String[] result = new String[wheels.size()];

        for (int i = 0; i < wheels.size(); i++)
        {
            Symbol symbol = wheels.get(i).currentSymbol();
            result[i] = (symbol != null) ? symbol.getColor() : null;
        }

        lastOperationOk = true;
        return result;
    }

    /**
     * Indica si todas las ruedas muestran el mismo símbolo. Si es así
     * y la máquina está visible, informa el jackpot al usuario.
     */
    public boolean isJackpot()
    {
        lastOperationOk = true;
        boolean jackpot = false;

        if (!wheels.isEmpty())
        {
            Symbol reference = wheels.get(0).currentSymbol();

            if (reference != null)
            {
                jackpot = true;

                for (Wheel wheel : wheels)
                {
                    if (!wheel.allSymbolMatch(reference))
                    {
                        jackpot = false;
                        break;
                    }
                }
            }
        }

        cabinet.changeColor(jackpot ? "gold" : "black");

        if (jackpot && visible)
        {
            JOptionPane.showMessageDialog(
                null,
                "¡JACKPOT! Todos los símbolos son iguales."
            );
        }

        return jackpot;
    }

    /**
     * Hace visible la máquina y todas sus ruedas.
     */
    public void makeVisible()
    {
        visible = true;
        cabinet.makeVisible();

        for (Wheel wheel : wheels)
        {
            wheel.makeVisible();
        }

        lastOperationOk = true;
    }

    /**
     * Oculta la máquina y todas sus ruedas.
     */
    public void makeInvisible()
    {
        visible = false;
        cabinet.makeInvisible();

        for (Wheel wheel : wheels)
        {
            wheel.makeInvisible();
        }

        lastOperationOk = true;
    }

    /**
     * Termina el simulador: oculta y elimina todas las ruedas.
     * No usa System.exit(0): eso cerraría la máquina virtual de Java
     * completa (y con ella BlueJ) mientras se está probando el
     * proyecto, lo cual no es deseable.
     */
    public void exit()
    {
        for (Wheel wheel : wheels)
        {
            wheel.makeInvisible();
        }

        cabinet.makeInvisible();
        wheels.clear();
        lastOperationOk = true;
    }

    /**
     * Indica si la última operación realizada se completó con éxito.
     */
    public boolean ok()
    {
        return lastOperationOk;
    }

    /**
     * Muestra un mensaje de error al usuario (solo si la máquina está
     * visible) y marca la última operación como fallida.
     */
    private void error(String message)
    {
        lastOperationOk = false;

        if (visible)
        {
            JOptionPane.showMessageDialog(null, message);
        }
    }
    
    /**
     * Bloquea una rueda, no permite hacer spin sobre ella
     */
    public void lock(int wheel){
        if(wheel < 1 || wheel >= wheels.size()){
            error("No existe esa rueda.");
            return;
        }
        wheels.get(wheel - 1).lock();
        lastOperationOk = true;
    }
    
    /**
     * Desbloquea una rueda pudiendo volver a usar spin sobre ella
     */
    public void unlock(int wheel){
        if (wheel < 1 || wheel >= wheels.size())
        {
            error("No existe esa rueda.");
            return;
        }

        wheels.get(wheel).unlock();
        lastOperationOk = true;
    }
    
    
    /**
     * Intercambia el contenido completo (todos los símbolos y cuál
     * está mostrando cada una) entre las ruedas wheel1 y wheel2.
     * Falla si alguna de las dos ruedas no existe, o si alguna de las
     * dos está bloqueada.
     */
    public void swap(int wheel1, int wheel2){
        if (wheel1 < 1 || wheel1 >= wheels.size() || wheel2 < 1 || wheel2 >= wheels.size()){
            error("No existe alguna de esas ruedas.");
            return;
        }

        Wheel w1 = wheels.get(wheel1-1);
        Wheel w2 = wheels.get(wheel2-1);

        if (w1.isLocked() || w2.isLocked()){
            error("No se puede intercambiar una rueda bloqueada.");
            return;
        }

        if (wheel1 != wheel2){
            w1.swapContent(w2);
        }

        lastOperationOk = true;
    } 
    
    /**
     * Recalcula el tamaño/posición del gabinete y reubica todas las
     * ruedas según su índice actual (1-based), para que queden
     * parejas y sin huecos tras un addWheel/delWheel.
     */
    private void layoutMachine(){
    for (int i = 0; i < wheels.size(); i++)
    {
        wheels.get(i).relocate(i + 1);
    }

    if (wheels.isEmpty())
    {
        cabinet.makeInvisible();
        return;
    }

    int width = (wheels.size() - 1) * Wheel.SPACING + Wheel.WIDTH + 2 * cabinetMargin;
    int height = Wheel.HEIGHT + 2 * cabinetMargin;
    int x = Wheel.BASE_X - cabinetMargin;
    int y = Wheel.BASE_Y - cabinetMargin;

    cabinet.changeSize(height, width);
    cabinet.moveHorizontal(x - cabinetX);
    cabinet.moveVertical(y - cabinetY);
    cabinetX = x;
    cabinetY = y;

    if (visible)
    {
        cabinet.makeVisible();

        // El cabinet acaba de redibujarse y quedó "encima" de todo;
        // volvemos a mostrar cada rueda para que queden por delante.
        for (Wheel wheel : wheels)
        {
            wheel.makeVisible();
        }
    }
    }

    /**
     * Ajusta pos al rango [1, max]: si es menor a 1 devuelve 1, si es
     * mayor a max devuelve max.
     */
    private int clampPosition(int pos, int max)
    {
        if (pos < 1)
        {
            return 1;
        }
        if (pos > max)
        {
            return max;
        }
        return pos;
    }
}
