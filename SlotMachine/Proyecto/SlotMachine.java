import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * Representa una máquina tragamonedas: administra un conjunto de
 * ruedas, permite girarlas, consultar sus símbolos y verificar si
 * se alcanzó el jackpot.
 *
 * Las posiciones de las ruedas se numeran desde 1. Si una posición es
 * menor a 1 se usa la 1 y si es mayor que la cantidad de ruedas se usa
 * la última. La máquina nace invisible; se muestra con makeVisible().
 * Los mensajes de error solo se presentan si la máquina está visible.
 *
 * Tipos de rueda: "normal", "lefty" (copia a la rueda de su izquierda al
 * girar), "rebel" (no se deja bloquear, intercambiar ni eliminar) y
 * "reverse" (gira al contrario). Tipos de símbolo: "normal",
 * "ephemeral" (se encoge en cada giro) y "shy" (alterna entre visible e
 * invisible cada vez que queda seleccionado).
 */
public final class SlotMachine
{
    private static final int BASE_X = 60;
    private static final int BASE_Y = 80;
    private static final int AREA_WIDTH = 900;
    private static final int MAX_SPACING = 90;
    private static final int MIN_SPACING = 4;
    private static final int MIN_SIZE = 4;
    private static final int MARGIN = 20;
    private static final String NORMAL_LOOK = "black";
    private static final String WINNING_LOOK = "gold";

    private ArrayList<Wheel> wheels;
    private boolean visible;
    private boolean lastOperationOk;
    private boolean winningLook;
    private Rectangle cabinet;
    private int cabinetX;
    private int cabinetY;

    /**
     * Crea una máquina sin ruedas, invisible.
     */
    public SlotMachine()
    {
        wheels = new ArrayList<Wheel>();
        visible = false;
        lastOperationOk = true;
        winningLook = false;
        cabinet = new Rectangle();
        cabinetX = 70;
        cabinetY = 15;
        cabinet.changeColor(NORMAL_LOOK);
    }

    /**
     * Crea una máquina invisible con n ruedas y n símbolos de colores
     * distintos en cada rueda. Todas las ruedas tienen los símbolos en el
     * mismo orden y cada una queda girada una cantidad aleatoria de
     * pasos; la configuración inicial nunca es ganadora (si n > 1).
     *
     * @param n cantidad de ruedas y de símbolos (mínimo 1)
     */
    public SlotMachine(int n)
    {
        this();
        int size = Math.max(1, n);
        String[] palette = createPalette(size);
        Random random = new Random();

        for (int pos = 1; pos <= size; pos++)
        {
            addWheel(pos);
            for (String color : palette)
            {
                addSymbol(pos, color);
            }
        }

        for (Wheel wheel : wheels)
        {
            wheel.spin(random.nextInt(size));
        }

        if (size > 1 && computeJackpot())
        {
            wheels.get(0).spin(1);
        }

        updateLook();
        lastOperationOk = true;
    }

    /**
     * Genera n colores hexadecimales distintos y los mezcla.
     */
    private static String[] createPalette(int n)
    {
        ArrayList<String> colors = new ArrayList<String>();

        for (int i = 0; i < n; i++)
        {
            float hue = (float) ((i * 0.61803398875) % 1.0);
            float saturation = 0.55f + 0.4f * ((i * 7) % 5) / 4f;
            float brightness = 0.65f + 0.3f * ((i * 3) % 4) / 3f;
            int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness) & 0xFFFFFF;

            while (colors.contains(String.format("#%06x", rgb)))
            {
                rgb = (rgb + 1) & 0xFFFFFF;
            }
            colors.add(String.format("#%06x", rgb));
        }

        Collections.shuffle(colors);
        return colors.toArray(new String[0]);
    }

    /**
     * Inserta una rueda nueva y vacía en la posición pos. Si pos es menor
     * a 1 se agrega al principio; si es mayor que la cantidad de ruedas
     * más uno, se agrega al final.
     *
     * @param pos posición (desde 1) que tendrá la nueva rueda
     */
    public void addWheel(int pos)
    {
        addWheel("normal", pos);
    }

    /**
     * Inserta una rueda nueva y vacía del tipo indicado en la posición
     * pos. Falla si el tipo no existe. Los tipos son "normal", "lefty",
     * "rebel" y "reverse" (sin importar mayúsculas).
     *
     * @param type tipo de la rueda
     * @param pos posición (desde 1) que tendrá la nueva rueda
     */
    public void addWheel(String type, int pos)
    {
        Wheel wheel = newWheel(type);

        if (wheel == null)
        {
            error("No existe el tipo de rueda indicado.");
            return;
        }

        int index = Math.max(1, Math.min(pos, wheels.size() + 1)) - 1;

        wheels.add(index, wheel);
        layoutMachine();

        if (visible)
        {
            wheel.makeVisible();
        }

        updateLook();
        lastOperationOk = true;
    }

    /**
     * Elimina la rueda que está en la posición pos (ajustada al rango
     * válido). Falla si la máquina no tiene ruedas o si la rueda no se
     * deja eliminar (rebel).
     *
     * @param pos posición (desde 1) de la rueda a eliminar
     */
    public void delWheel(int pos)
    {
        Wheel wheel = wheelAt(pos);

        if (wheel == null)
        {
            error("No existe esa rueda.");
            return;
        }
        if (!wheel.isRemovable())
        {
            error("Una rueda " + wheel.type() + " no se deja eliminar.");
            return;
        }

        wheel.makeInvisible();
        wheels.remove(wheel);
        layoutMachine();
        updateLook();
        lastOperationOk = true;
    }

    /**
     * Crea un símbolo del color indicado y lo agrega al final de la rueda
     * pos. Falla si no hay ruedas, si el color no es un color CSS válido
     * o si la rueda ya tiene un símbolo de ese color.
     *
     * @param pos posición (desde 1) de la rueda
     * @param color nombre CSS (por ejemplo "red") o valor "#rrggbb"
     */
    public void addSymbol(int pos, String color)
    {
        addSymbol("normal", pos, color);
    }

    /**
     * Crea un símbolo del tipo y color indicados y lo agrega al final de
     * la rueda pos. Falla si no hay ruedas, si el color no es un color
     * CSS válido, si el tipo no existe ("normal", "ephemeral" o "shy") o
     * si la rueda ya tiene un símbolo de ese color.
     *
     * @param type tipo del símbolo
     * @param pos posición (desde 1) de la rueda
     * @param color nombre CSS (por ejemplo "red") o valor "#rrggbb"
     */
    public void addSymbol(String type, int pos, String color)
    {
        Wheel wheel = wheelAt(pos);
        String normalized = Symbol.normalize(color);

        if (wheel == null)
        {
            error("No existe esa rueda.");
            return;
        }
        if (!Canvas.isValidColor(normalized))
        {
            error("El color no es válido.");
            return;
        }

        Symbol symbol = newSymbol(type, normalized);

        if (symbol == null)
        {
            error("No existe el tipo de símbolo indicado.");
            return;
        }
        if (wheel.hasSymbol(normalized))
        {
            error("La rueda ya tiene un símbolo de ese color.");
            return;
        }

        wheel.addSymbol(symbol);
        updateLook();
        lastOperationOk = true;
    }

    /**
     * Elimina el símbolo del color indicado de todas las ruedas en las
     * que aparezca. Falla si ninguna rueda tiene ese símbolo.
     *
     * @param symbol color del símbolo a eliminar
     */
    public void delSymbol(String symbol)
    {
        String color = Symbol.normalize(symbol);
        boolean removed = false;

        for (Wheel wheel : wheels)
        {
            removed = wheel.delSymbol(color) || removed;
        }

        if (!removed)
        {
            error("Ninguna rueda tiene un símbolo de ese color.");
            return;
        }

        updateLook();
        lastOperationOk = true;
    }

    /**
     * Hace que la rueda indicada muestre el símbolo del color dado. Falla
     * si la rueda no existe, está bloqueada o no tiene ese símbolo.
     *
     * @param wheel posición (desde 1) de la rueda
     * @param symbol color del símbolo que debe mostrarse
     */
    public void placeSymbol(int wheel, String symbol)
    {
        Wheel target = wheelAt(wheel);

        if (target == null)
        {
            error("No existe esa rueda.");
            return;
        }
        if (target.isLocked())
        {
            error("No se puede modificar una rueda bloqueada.");
            return;
        }
        if (!target.placeSymbol(Symbol.normalize(symbol)))
        {
            error("Esa rueda no tiene un símbolo de ese color.");
            return;
        }

        updateLook();
        lastOperationOk = true;
    }

    /**
     * Gira una rueda en particular, pasando a su siguiente símbolo.
     *
     * @param wheel posición (desde 1) de la rueda
     */
    public void spin(int wheel)
    {
        spin(wheel, 1);
    }

    /**
     * Gira todas las ruedas no bloqueadas, una posición cada una, de
     * izquierda a derecha (una rueda lefty copia a su vecina ya girada).
     */
    public void spin()
    {
        for (Wheel wheel : wheels)
        {
            if (!wheel.isLocked() && !wheel.follow(leftOf(wheel)))
            {
                wheel.newCurrentSymbol();
            }
        }

        updateLook();
        lastOperationOk = true;
    }

    /**
     * Gira una rueda una cantidad de pasos. Los pasos pueden ser
     * negativos (giro en sentido contrario) o mayores que la cantidad de
     * símbolos. Si la máquina está visible el giro se ve paso a paso.
     * Falla si la rueda no existe o está bloqueada.
     *
     * @param wheel posición (desde 1) de la rueda
     * @param steps cantidad de pasos
     */
    public void spin(int wheel, int steps)
    {
        Wheel target = wheelAt(wheel);

        if (target == null)
        {
            error("No existe esa rueda.");
            return;
        }
        if (target.isLocked())
        {
            error("No se puede girar una rueda bloqueada.");
            return;
        }

        turn(target, steps);
        updateLook();
        lastOperationOk = true;
    }

    /**
     * Deja la máquina en una configuración dada: cada rueda muestra el
     * símbolo del color que le corresponde. Es atómico: si alguna rueda
     * está bloqueada, no tiene el color o la cantidad de colores no
     * coincide con la de ruedas, no se modifica ninguna rueda.
     *
     * @param setSymbols colores que debe mostrar cada rueda, de
     *                   izquierda a derecha
     */
    public void spin(String[] setSymbols)
    {
        if (setSymbols == null || setSymbols.length != wheels.size())
        {
            error("La cantidad de colores no coincide con la de ruedas.");
            return;
        }

        for (int i = 0; i < setSymbols.length; i++)
        {
            Wheel wheel = wheels.get(i);

            if (wheel.isLocked())
            {
                error("No se puede modificar una rueda bloqueada.");
                return;
            }
            if (!wheel.hasSymbol(Symbol.normalize(setSymbols[i])))
            {
                error("Una rueda no tiene un símbolo de ese color.");
                return;
            }
        }

        for (int i = 0; i < setSymbols.length; i++)
        {
            wheels.get(i).placeSymbol(Symbol.normalize(setSymbols[i]));
        }

        updateLook();
        lastOperationOk = true;
    }

    /**
     * Colores de todos los símbolos de la máquina, rueda por rueda
     * (de la primera a la última) y en el orden en que están en cada
     * rueda.
     *
     * @return los colores de todos los símbolos
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
     * Cantidad de colores distintos entre los símbolos que las ruedas
     * están mostrando en este momento (la configuración actual). Con ella
     * la máquina sirve como herramienta de prueba: vale 1 cuando todas
     * las ruedas muestran el mismo símbolo.
     *
     * @return la cantidad de símbolos visibles distintos
     */
    public int distinctSymbols()
    {
        int distinct = Symbol.getDistinctSymbols(configuration());
        lastOperationOk = true;
        return distinct;
    }

    /**
     * Colores que muestran las ruedas, de izquierda a derecha (una
     * entrada por rueda; null si la rueda no tiene símbolos o si el
     * símbolo que muestra está invisible).
     *
     * @return la configuración actual de la máquina
     */
    public String[] configuration()
    {
        String[] result = new String[wheels.size()];

        for (int i = 0; i < wheels.size(); i++)
        {
            Symbol symbol = wheels.get(i).shownSymbol();
            result[i] = (symbol != null) ? symbol.getColor() : null;
        }

        lastOperationOk = true;
        return result;
    }

    /**
     * Indica si todas las ruedas muestran el mismo símbolo. Cuando es así
     * la máquina luce distinta (gabinete dorado).
     *
     * @return true si la configuración es ganadora
     */
    public boolean isJackpot()
    {
        updateLook();
        lastOperationOk = true;
        return winningLook;
    }

    /**
     * Hace visible la máquina y todas sus ruedas.
     */
    public void makeVisible()
    {
        visible = true;
        redrawAll();
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
     * Termina el simulador: oculta y elimina todas las ruedas. No usa
     * System.exit(0) porque cerraría la máquina virtual (y BlueJ).
     */
    public void exit()
    {
        makeInvisible();
        wheels.clear();
        winningLook = false;
        cabinet.changeColor(NORMAL_LOOK);
        lastOperationOk = true;
    }

    /**
     * Indica si la última operación realizada se completó con éxito.
     *
     * @return true si la última operación tuvo éxito
     */
    public boolean ok()
    {
        return lastOperationOk;
    }

    /**
     * Bloquea una rueda: no se podrá girar, intercambiar ni cambiar el
     * símbolo que muestra hasta que se desbloquee. Falla si la rueda no
     * existe o no se deja bloquear (rebel).
     *
     * @param wheel posición (desde 1) de la rueda
     * @return la posición (desde 1) de la rueda que quedó bloqueada, que
     *         puede diferir de wheel si estaba fuera de rango; 0 si no se
     *         pudo bloquear
     */
    public int lock(int wheel)
    {
        Wheel target = wheelAt(wheel);

        if (target == null)
        {
            error("No existe esa rueda.");
            return 0;
        }

        if (!target.isLockable())
        {
            error("Una rueda " + target.type() + " no se deja bloquear.");
            return 0;
        }

        target.lock();
        lastOperationOk = true;
        return wheels.indexOf(target) + 1;
    }

    /**
     * Desbloquea una rueda para poder operar sobre ella otra vez.
     *
     * @param wheel posición (desde 1) de la rueda
     */
    public void unlock(int wheel)
    {
        Wheel target = wheelAt(wheel);

        if (target == null)
        {
            error("No existe esa rueda.");
            return;
        }

        target.unlock();
        lastOperationOk = true;
    }

    /**
     * Intercambia el contenido completo (todos los símbolos y cuál está
     * mostrando cada una) entre las ruedas wheel1 y wheel2. Falla si no
     * hay ruedas, si alguna de las dos está bloqueada o si alguna no se
     * deja intercambiar (rebel).
     *
     * @param wheel1 posición (desde 1) de la primera rueda
     * @param wheel2 posición (desde 1) de la segunda rueda
     */
    public void swap(int wheel1, int wheel2)
    {
        Wheel first = wheelAt(wheel1);
        Wheel second = wheelAt(wheel2);

        if (first == null || second == null)
        {
            error("No existe alguna de esas ruedas.");
            return;
        }
        if (first.isLocked() || second.isLocked())
        {
            error("No se puede intercambiar una rueda bloqueada.");
            return;
        }
        if (!first.isSwappable() || !second.isSwappable())
        {
            error("Una rueda rebel no se deja intercambiar.");
            return;
        }

        if (first != second)
        {
            first.swapContent(second);
        }

        updateLook();
        lastOperationOk = true;
    }

    /**
     * Rueda que está en la posición pos, ajustada al rango [1, ruedas].
     *
     * @return la rueda, o null si la máquina no tiene ruedas
     */
    private Wheel wheelAt(int pos)
    {
        if (wheels.isEmpty())
        {
            return null;
        }

        return wheels.get(Math.max(1, Math.min(pos, wheels.size())) - 1);
    }

    /**
     * Crea una rueda del tipo indicado.
     *
     * @return la rueda nueva, o null si el tipo no existe
     */
    private Wheel newWheel(String type)
    {
        String kind = (type == null) ? "" : type.trim().toLowerCase();

        switch (kind)
        {
            case "normal":
                return new Wheel();
            case "lefty":
                return new LeftyWheel();
            case "rebel":
                return new RebelWheel();
            case "reverse":
                return new ReverseWheel();
            default:
                return null;
        }
    }

    /**
     * Crea un símbolo del tipo y color indicados.
     *
     * @return el símbolo nuevo, o null si el tipo no existe
     */
    private Symbol newSymbol(String type, String color)
    {
        String kind = (type == null) ? "" : type.trim().toLowerCase();

        switch (kind)
        {
            case "normal":
                return new Symbol(color);
            case "ephemeral":
                return new EphemeralSymbol(color);
            case "shy":
                return new ShySymbol(color);
            default:
                return null;
        }
    }

    /**
     * Rueda que está a la izquierda de la dada.
     *
     * @return la rueda vecina, o null si es la primera
     */
    private Wheel leftOf(Wheel wheel)
    {
        int index = wheels.indexOf(wheel);

        return (index > 0) ? wheels.get(index - 1) : null;
    }

    /**
     * Gira una rueda: si es de las que copian a su vecina (lefty) la
     * copia; si no, gira la cantidad de pasos.
     */
    private void turn(Wheel target, int steps)
    {
        if (steps != 0 && target.follow(leftOf(target)))
        {
            return;
        }

        target.spin(steps);
    }

    /**
     * Calcula si todas las ruedas muestran el mismo símbolo.
     */
    private boolean computeJackpot()
    {
        if (wheels.isEmpty())
        {
            return false;
        }

        Symbol reference = wheels.get(0).shownSymbol();

        for (Wheel wheel : wheels)
        {
            if (!wheel.allSymbolMatch(reference))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Cambia el aspecto del gabinete (dorado si la configuración es
     * ganadora, negro si no) solo cuando el estado cambia.
     */
    private void updateLook()
    {
        boolean winning = computeJackpot();

        if (winning != winningLook)
        {
            winningLook = winning;
            cabinet.changeColor(winning ? WINNING_LOOK : NORMAL_LOOK);
            redrawAll();
        }
    }

    /**
     * Redibuja el gabinete y luego las ruedas, para que el gabinete
     * quede detrás. No hace nada si la máquina es invisible.
     */
    private void redrawAll()
    {
        if (!visible)
        {
            return;
        }

        if (wheels.isEmpty())
        {
            cabinet.makeInvisible();
            return;
        }

        cabinet.makeVisible();

        for (Wheel wheel : wheels)
        {
            wheel.makeVisible();
        }
    }

    /**
     * Reubica y redimensiona gabinete y ruedas para que queden parejas y
     * sin huecos después de agregar o eliminar una rueda.
     */
    private void layoutMachine()
    {
        int count = wheels.size();

        if (count == 0)
        {
            redrawAll();
            return;
        }

        int spacing = Math.max(MIN_SPACING, Math.min(MAX_SPACING, AREA_WIDTH / count));
        int size = Math.max(MIN_SIZE, spacing * 2 / 3);

        for (int i = 0; i < count; i++)
        {
            wheels.get(i).layout(BASE_X + i * spacing, BASE_Y, size, size);
        }

        int x = BASE_X - MARGIN;
        int y = BASE_Y - MARGIN;
        cabinet.changeSize(size + 2 * MARGIN, (count - 1) * spacing + size + 2 * MARGIN);
        cabinet.moveHorizontal(x - cabinetX);
        cabinet.moveVertical(y - cabinetY);
        cabinetX = x;
        cabinetY = y;
        redrawAll();
    }

    /**
     * Marca la última operación como fallida y muestra el mensaje al
     * usuario solo si la máquina está visible.
     */
    private void error(String message)
    {
        lastOperationOk = false;

        if (visible)
        {
            JOptionPane.showMessageDialog(null, message);
        }
    }
}
