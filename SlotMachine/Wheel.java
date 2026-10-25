import java.util.ArrayList;

/**
 * Representa una rueda normal de la máquina tragamonedas.
 * Cada rueda mantiene su propia lista de símbolos y recuerda cuál de
 * ellos está mostrando actualmente. Todos los símbolos de una rueda
 * comparten la misma ventana, así que solo el símbolo actual se dibuja.
 *
 * Es la base de los demás tipos de rueda (LeftyWheel, RebelWheel y
 * ReverseWheel), que la extienden redefiniendo los ganchos type(),
 * idleColor(), direction(), follow(), isLockable(), isSwappable() e
 * isRemovable().
 */
public class Wheel
{
    private static final String LOCKED_COLOR = "lightgray";
    private static final int FAST_PAUSE = 40;
    private static final int SLOW_PAUSE = 150;
    private static final int SLOW_STEPS = 3;
    private static final int SLOT_DEFAULT_X = 70;
    private static final int SLOT_DEFAULT_Y = 15;

    private ArrayList<Symbol> symbols;
    private Rectangle slot;
    private int currentPosition;
    private int xPosition;
    private int yPosition;
    private int width;
    private int height;
    private boolean locked;
    private boolean visible;

    /**
     * Crea una rueda vacía con la geometría por defecto.
     */
    public Wheel()
    {
        this(60, 80, 60, 60);
    }

    /**
     * Crea una rueda vacía con la ventana en la posición y tamaño dados.
     *
     * @param x coordenada horizontal de la ventana
     * @param y coordenada vertical de la ventana
     * @param width ancho de la ventana
     * @param height alto de la ventana
     */
    public Wheel(int x, int y, int width, int height)
    {
        symbols = new ArrayList<Symbol>();
        currentPosition = 0;
        locked = false;
        visible = false;
        slot = new Rectangle();
        xPosition = SLOT_DEFAULT_X;
        yPosition = SLOT_DEFAULT_Y;
        slot.changeColor("white");
        layout(x, y, width, height);
    }

    /**
     * Reubica y redimensiona la ventana de la rueda y todos sus símbolos.
     * Se usa cada vez que cambia la cantidad de ruedas de la máquina.
     *
     * @param x nueva coordenada horizontal de la ventana
     * @param y nueva coordenada vertical de la ventana
     * @param newWidth nuevo ancho de la ventana
     * @param newHeight nuevo alto de la ventana
     */
    final void layout(int x, int y, int newWidth, int newHeight)
    {
        slot.changeSize(newHeight, newWidth);
        slot.moveHorizontal(x - xPosition);
        slot.moveVertical(y - yPosition);
        xPosition = x;
        yPosition = y;
        width = newWidth;
        height = newHeight;

        for (Symbol symbol : symbols)
        {
            fit(symbol);
        }
        refresh();
    }

    /**
     * Ajusta el tamaño y la posición de un símbolo para que quede
     * centrado dentro de la ventana de esta rueda.
     */
    private void fit(Symbol symbol)
    {
        int diameter = Math.max(2, Math.min(width, height) * 3 / 4);
        symbol.setDiameter(diameter);
        symbol.setPosition(xPosition + (width - diameter) / 2,
                           yPosition + (height - diameter) / 2);
    }

    /**
     * Bloquea la rueda: no podrá girarse, intercambiarse ni cambiar el
     * símbolo que muestra hasta que se desbloquee.
     */
    public void lock()
    {
        locked = true;
        slot.changeColor(LOCKED_COLOR);
        refresh();
    }

    /**
     * Desbloquea la rueda para que se pueda operar de nuevo.
     */
    public void unlock()
    {
        locked = false;
        slot.changeColor(idleColor());
        refresh();
    }

    /**
     * Indica si la rueda está bloqueada.
     *
     * @return true si la rueda está bloqueada
     */
    public boolean isLocked()
    {
        return locked;
    }

    /**
     * Agrega un símbolo al final de esta rueda. Solo SlotMachine decide
     * qué símbolos tiene cada rueda (visibilidad de paquete).
     *
     * @param symbol símbolo a agregar
     */
    void addSymbol(Symbol symbol)
    {
        fit(symbol);
        symbols.add(symbol);
        refresh();
    }

    /**
     * Elimina de esta rueda el símbolo del color indicado. Si era el
     * símbolo que se estaba mostrando, la rueda pasa a mostrar el que
     * ocupaba el siguiente lugar.
     *
     * @param color color del símbolo a eliminar
     * @return true si la rueda tenía ese símbolo y fue eliminado
     */
    boolean delSymbol(String color)
    {
        int index = indexOf(color);

        if (index < 0)
        {
            return false;
        }

        symbols.get(index).makeInvisible();
        symbols.remove(index);

        if (index < currentPosition)
        {
            currentPosition--;
        }
        if (currentPosition >= symbols.size())
        {
            currentPosition = 0;
        }

        refresh();
        return true;
    }

    /**
     * Busca la posición (desde 0) del símbolo de un color en esta rueda.
     *
     * @param color color buscado
     * @return la posición del símbolo, o -1 si la rueda no lo tiene
     */
    private int indexOf(String color)
    {
        for (int i = 0; i < symbols.size(); i++)
        {
            if (symbols.get(i).getColor().equals(color))
            {
                return i;
            }
        }

        return -1;
    }

    /**
     * Indica si la rueda tiene un símbolo del color dado.
     *
     * @param color color buscado
     * @return true si la rueda tiene ese símbolo
     */
    boolean hasSymbol(String color)
    {
        return indexOf(color) >= 0;
    }

    /**
     * Hace que la rueda muestre el símbolo del color indicado.
     *
     * @param color color del símbolo que debe mostrarse
     * @return true si lo encontró y lo colocó; false si la rueda no
     *         tiene un símbolo de ese color
     */
    boolean placeSymbol(String color)
    {
        int index = indexOf(color);

        if (index < 0)
        {
            return false;
        }

        boolean changed = index != currentPosition;

        currentPosition = index;
        if (changed)
        {
            symbols.get(index).selected();
        }
        refresh();
        return true;
    }

    /**
     * Devuelve una copia de la lista de símbolos de esta rueda, para no
     * exponer el estado interno.
     *
     * @return copia de la lista de símbolos
     */
    ArrayList<Symbol> getSymbols()
    {
        return new ArrayList<Symbol>(symbols);
    }

    /**
     * Símbolo que esta rueda muestra actualmente.
     *
     * @return el símbolo actual, o null si la rueda no tiene símbolos
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
     * Símbolo que esta rueda muestra y que además está visible: es null
     * si la rueda está vacía o si el símbolo actual está oculto (por
     * ejemplo, un ShySymbol invisible).
     *
     * @return el símbolo visible, o null si no hay ninguno
     */
    public Symbol shownSymbol()
    {
        Symbol current = currentSymbol();

        return (current == null || current.isHidden()) ? null : current;
    }

    /**
     * Avanza un símbolo (de forma cíclica, en el sentido de giro de la
     * rueda) y lo muestra. Cuenta como un giro.
     *
     * @return el nuevo símbolo actual, o null si la rueda está vacía
     */
    public Symbol newCurrentSymbol()
    {
        if (symbols.isEmpty())
        {
            return null;
        }

        move(direction());
        landed();
        return symbols.get(currentPosition);
    }

    /**
     * Mueve la rueda una cantidad de lugares (positiva o negativa) y
     * muestra el símbolo que queda en la ventana.
     */
    private Symbol move(int places)
    {
        if (symbols.isEmpty())
        {
            return null;
        }

        currentPosition = Math.floorMod(currentPosition + places, symbols.size());
        refresh();
        return symbols.get(currentPosition);
    }

    /**
     * Gira la rueda una cantidad de pasos; cada paso muestra el símbolo
     * siguiente (o el anterior si steps es negativo). Si la rueda está
     * visible, el giro se ve paso a paso por el camino más corto y los
     * últimos pasos son más lentos para dar sensación de frenado. Con
     * cero pasos no hay giro. Al terminar, los símbolos se enteran del
     * giro y el símbolo que quedó en la ventana, de su selección.
     *
     * @param steps cantidad de pasos (puede ser negativa o mayor que la
     *              cantidad de símbolos)
     */
    public void spin(int steps)
    {
        if (symbols.isEmpty() || steps == 0)
        {
            return;
        }

        int forward = Math.floorMod(steps, symbols.size());

        if (direction() < 0)
        {
            forward = Math.floorMod(-forward, symbols.size());
        }

        if (visible)
        {
            animate(forward);
        }
        else
        {
            move(forward);
        }

        landed();
    }

    /**
     * Mueve la rueda visible paso a paso por el camino más corto.
     */
    private void animate(int forward)
    {
        int backward = symbols.size() - forward;
        int direction = (forward <= backward) ? 1 : -1;
        int count = Math.min(forward, backward);

        for (int i = 0; i < count; i++)
        {
            move(direction);
            pause(count - i);
        }
    }

    /**
     * Avisa a los símbolos que la rueda giró y selecciona el que quedó
     * en la ventana (por ejemplo, un símbolo tímido alterna su estado).
     */
    private void landed()
    {
        for (Symbol symbol : symbols)
        {
            symbol.spun();
        }

        symbols.get(currentPosition).selected();
        refresh();
    }

    /**
     * Copia el estado de otra rueda: muestra el símbolo del mismo color
     * que ella (si esta rueda lo tiene) y cuenta como un giro. Por
     * defecto una rueda no copia a nadie y devuelve false.
     *
     * @param left la rueda que está a su izquierda (null si no hay)
     * @return true si copió a la otra rueda; false si debe girar normal
     */
    boolean follow(Wheel left)
    {
        return false;
    }

    /**
     * Muestra el símbolo del color dado como resultado de un giro.
     * Debe ser un color que la rueda tenga.
     */
    void jumpTo(String color)
    {
        currentPosition = indexOf(color);
        landed();
    }

    /**
     * Nombre del tipo de esta rueda.
     *
     * @return "normal"
     */
    public String type()
    {
        return "normal";
    }

    /**
     * Gancho: sentido en que gira la rueda.
     *
     * @return 1 si gira hacia adelante (-1 si lo hace al contrario)
     */
    protected int direction()
    {
        return 1;
    }

    /**
     * Pinta la ventana con el color de su tipo. Lo llaman los
     * constructores de las subclases, porque el constructor de Wheel no
     * debe invocar métodos redefinibles.
     */
    final void applyIdleColor()
    {
        slot.changeColor(idleColor());
    }

    /**
     * Gancho: color de la ventana cuando la rueda no está bloqueada.
     *
     * @return color CSS de la ventana libre
     */
    protected String idleColor()
    {
        return "white";
    }

    /**
     * Indica si la rueda se deja bloquear.
     *
     * @return true si se puede bloquear
     */
    public boolean isLockable()
    {
        return true;
    }

    /**
     * Indica si la rueda se deja intercambiar con otra.
     *
     * @return true si se puede intercambiar
     */
    public boolean isSwappable()
    {
        return true;
    }

    /**
     * Indica si la rueda se deja eliminar de la máquina.
     *
     * @return true si se puede eliminar
     */
    public boolean isRemovable()
    {
        return true;
    }

    /**
     * Espera entre dos pasos de un giro visible; los últimos pasos
     * esperan más.
     */
    private void pause(int remaining)
    {
        int time = (remaining <= SLOW_STEPS) ? SLOW_PAUSE : FAST_PAUSE;
        Canvas.getCanvas().wait(time);
    }

    /**
     * Intercambia el contenido de esta rueda con el de other: la lista
     * de símbolos y cuál de ellos se está mostrando. Cada ventana se
     * queda en su lugar; solo cambia lo que hay dentro.
     *
     * @param other la otra rueda
     */
    public void swapContent(Wheel other)
    {
        ArrayList<Symbol> tempSymbols = this.symbols;
        int tempPosition = this.currentPosition;

        this.symbols = other.symbols;
        this.currentPosition = other.currentPosition;
        other.symbols = tempSymbols;
        other.currentPosition = tempPosition;

        this.adoptSymbols();
        other.adoptSymbols();
    }

    /**
     * Reubica todos los símbolos de la rueda dentro de su ventana y
     * actualiza lo que se dibuja.
     */
    private void adoptSymbols()
    {
        for (Symbol symbol : symbols)
        {
            symbol.makeInvisible();
            fit(symbol);
        }
        refresh();
    }

    /**
     * Indica si el símbolo visible de esta rueda tiene el mismo color
     * que el símbolo recibido. Se usa al verificar el jackpot; una rueda
     * vacía o con su símbolo oculto no coincide con nadie.
     *
     * @param other símbolo con el que se compara
     * @return true si ambos tienen el mismo color
     */
    public boolean allSymbolMatch(Symbol other)
    {
        Symbol mine = shownSymbol();

        if (mine == null || other == null)
        {
            return false;
        }

        return mine.getColor().equals(other.getColor());
    }

    /**
     * Muestra la ventana de la rueda y su símbolo actual.
     */
    public void makeVisible()
    {
        visible = true;
        slot.makeVisible();
        refresh();
    }

    /**
     * Oculta la ventana de la rueda y todos sus símbolos.
     */
    public void makeInvisible()
    {
        visible = false;
        slot.makeInvisible();

        for (Symbol symbol : symbols)
        {
            symbol.makeInvisible();
        }
    }

    /**
     * Si la rueda es visible, dibuja solo el símbolo actual y oculta los
     * demás. No hace nada si la rueda es invisible.
     */
    private void refresh()
    {
        if (!visible)
        {
            return;
        }

        for (int i = 0; i < symbols.size(); i++)
        {
            if (i != currentPosition)
            {
                symbols.get(i).makeInvisible();
            }
        }

        if (!symbols.isEmpty())
        {
            symbols.get(currentPosition).makeVisible();
        }
    }
}
