import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Representa una máquina tragamonedas: administra un conjunto de
 * ruedas, permite girarlas, consultar sus símbolos y verificar si
 * se alcanzó el jackpot.
 */
public class SlotMachine
{
    private ArrayList<Wheel> wheels;
    private boolean visible;
    private boolean lastOperationOk;

    /**
     * Crea una máquina sin ruedas, visible por defecto.
     */
    public SlotMachine()
    {
        wheels = new ArrayList<Wheel>();
        visible = true;
        lastOperationOk = true;
    }

    /**
     * Inserta una rueda nueva y vacía en la posición pos.
     */
    public void addWheel(int pos)
    {
        if (pos < 0 || pos > wheels.size())
        {
            error("Posición de rueda inválida.");
            return;
        }

        Wheel wheel = new Wheel(50 + pos * 70, 50);
        wheels.add(pos, wheel);

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
        if (pos < 0 || pos >= wheels.size())
        {
            error("No existe esa rueda.");
            return;
        }

        wheels.get(pos).makeInvisible();
        wheels.remove(pos);
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
        wheel.addSymbol(new Symbol(color, 0));

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
        if (wheel < 0 || wheel >= wheels.size())
        {
            error("No existe esa rueda.");
            return;
        }

        boolean placed = wheels.get(wheel).placeSymbol(symbol);

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
    public void spin(int wheel){
        if (wheel < 0 || wheel >= wheels.size())
        {
            error("No existe esa rueda.");
            return;
        }
        
        if (wheels.get(wheel).isLocked()){
            error("No se puede girar una rueda bloqueada.");
            return;
        }

        wheels.get(wheel).newCurrentSymbol();
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

        if (wheels.isEmpty())
        {
            return false;
        }

        Symbol reference = wheels.get(0).currentSymbol();

        if (reference == null)
        {
            return false;
        }

        for (Wheel wheel : wheels)
        {
            if (!wheel.allSymbolMatch(reference))
            {
                return false;
            }
        }

        if (visible)
        {
            JOptionPane.showMessageDialog(
                null,
                "¡JACKPOT! Todos los símbolos son iguales."
            );
        }

        return true;
    }

    /**
     * Hace visible la máquina y todas sus ruedas.
     */
    public void makeVisible()
    {
        visible = true;

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
        if(wheel < 0 || wheel >= wheels.size()){
            error("No existe esa rueda.");
            return;
        }
        wheels.get(wheel).lock();
        lastOperationOk = true;
    }
    
    /**
     * Desbloquea una rueda pudiendo volver a usar spin sobre ella
     */
    public void unlock(int wheel){
        if (wheel < 0 || wheel >= wheels.size())
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
        if (wheel1 < 0 || wheel1 >= wheels.size() || wheel2 < 0 || wheel2 >= wheels.size()){
        error("No existe alguna de esas ruedas.");
        return;
        }

        Wheel w1 = wheels.get(wheel1);
        Wheel w2 = wheels.get(wheel2);

        if (w1.isLocked() || w2.isLocked()){
            error("No se puede intercambiar una rueda bloqueada.");
            return;
        }

        if (wheel1 != wheel2){
        w1.swapContent(w2);
        }

        lastOperationOk = true;
    } 
}