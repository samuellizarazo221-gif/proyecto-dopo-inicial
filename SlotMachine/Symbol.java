import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Representa un símbolo de la máquina tragamonedas. Un símbolo se
 * identifica por su color (nombre CSS o valor hexadecimal) y se dibuja
 * como un círculo de ese color (Circle de shapes).
 *
 * Esta clase es también el tipo "normal" y la base de los demás tipos de
 * símbolos (EphemeralSymbol, ShySymbol). Cada tipo reacciona de forma
 * distinta a dos eventos de su rueda: selected() (el símbolo queda en la
 * ventana de la rueda) y spun() (la rueda giró). Los tipos distintos
 * del normal se dibujan con un aro de color alrededor para poder
 * distinguirlos. Para agregar un tipo nuevo basta crear la subclase y
 * registrarla en TYPES.
 */
public class Symbol
{
    private static final int DEFAULT_X = 20;
    private static final int DEFAULT_Y = 15;
    private static final int DEFAULT_DIAMETER = 30;
    private static final Map<String, Function<String, Symbol>> TYPES =
        new LinkedHashMap<String, Function<String, Symbol>>();

    static
    {
        TYPES.put("normal", Symbol::new);
        TYPES.put("ephemeral", EphemeralSymbol::new);
        TYPES.put("shy", ShySymbol::new);
    }

    private String color;
    private Ring body;
    private Ring halo;
    private int boxX;
    private int boxY;
    private int fullDiameter;
    private boolean drawn;

    /**
     * Crea un símbolo normal del color indicado. El color se guarda sin
     * espacios y en minúsculas para que dos símbolos del mismo color
     * siempre sean comparables.
     *
     * @param color nombre CSS (por ejemplo "red") o valor "#rrggbb"
     */
    public Symbol(String color)
    {
        this(color, null);
    }

    /**
     * Crea un símbolo con un aro de color que lo distingue de los demás
     * tipos. Lo usan las subclases.
     *
     * @param color nombre CSS o valor "#rrggbb" del símbolo
     * @param haloColor color del aro, o null si no lleva aro
     */
    Symbol(String color, String haloColor)
    {
        this.color = normalize(color);
        body = new Ring(this.color);
        halo = (haloColor == null) ? null : new Ring(haloColor);
        boxX = DEFAULT_X;
        boxY = DEFAULT_Y;
        fullDiameter = DEFAULT_DIAMETER;
        drawn = false;
    }

    /**
     * Crea un símbolo del tipo y color indicados.
     *
     * @param type "normal", "ephemeral" o "shy" (sin importar mayúsculas)
     * @param color nombre CSS o valor "#rrggbb"
     * @return el símbolo, o null si el tipo no existe
     */
    public static Symbol create(String type, String color)
    {
        String name = (type == null) ? null : type.trim().toLowerCase();
        Function<String, Symbol> maker = TYPES.get(name);

        return (maker == null) ? null : maker.apply(color);
    }

    /**
     * Normaliza un color: lo recorta y lo pasa a minúsculas.
     *
     * @param color color a normalizar (puede ser null)
     * @return el color normalizado, o null si color es null
     */
    public static String normalize(String color)
    {
        return (color == null) ? null : color.trim().toLowerCase();
    }

    /**
     * Color de este símbolo.
     *
     * @return el color normalizado del símbolo
     */
    public String getColor()
    {
        return color;
    }

    /**
     * Tipo de este símbolo.
     *
     * @return "normal" en esta clase; cada subclase devuelve el suyo
     */
    public String getType()
    {
        return "normal";
    }

    /**
     * Indica si el símbolo está oculto aunque esté en la ventana de su
     * rueda. Solo los símbolos tímidos pueden estar ocultos.
     *
     * @return true si el símbolo está oculto
     */
    public boolean isHidden()
    {
        return false;
    }

    /**
     * Ubica el símbolo en una coordenada absoluta (esquina superior
     * izquierda del espacio que ocupa). Internamente calcula el
     * desplazamiento necesario, porque los movimientos de shapes son
     * relativos.
     *
     * @param x coordenada horizontal en píxeles
     * @param y coordenada vertical en píxeles
     */
    public void setPosition(int x, int y)
    {
        boxX = x;
        boxY = y;
        refresh();
    }

    /**
     * Cambia el diámetro con el que se dibuja el símbolo.
     *
     * @param newDiameter nuevo diámetro en píxeles (mayor que cero)
     */
    public void setDiameter(int newDiameter)
    {
        fullDiameter = Math.max(1, newDiameter);
        refresh();
    }

    /**
     * Diámetro con el que se dibuja el símbolo en este momento. En un
     * símbolo efímero es menor que el que le asignó su rueda.
     *
     * @return el diámetro en píxeles
     */
    public int getDiameter()
    {
        return Math.max(1, shownDiameter());
    }

    /**
     * Hace visible el símbolo (si su tipo lo permite en este momento).
     */
    public void makeVisible()
    {
        drawn = true;
        repaint();
    }

    /**
     * Oculta el símbolo.
     */
    public void makeInvisible()
    {
        drawn = false;
        repaint();
    }

    /**
     * Cuenta cuántos colores distintos (ignorando nulos) hay en el
     * arreglo recibido. Se usa para saber cuántos símbolos diferentes
     * muestra la máquina.
     *
     * @param colors colores a revisar (pueden incluir nulos)
     * @return la cantidad de colores distintos
     */
    public static int getDistinctSymbols(String[] colors)
    {
        java.util.HashSet<String> distinct = new java.util.HashSet<String>();

        for (String color : colors)
        {
            if (color != null)
            {
                distinct.add(color);
            }
        }

        return distinct.size();
    }

    /**
     * Ajusta el símbolo para que quede centrado dentro de la ventana de
     * su rueda, ocupando tres cuartas partes de la ventana.
     */
    void fitIn(int x, int y, int width, int height)
    {
        fullDiameter = Math.max(2, Math.min(width, height) * 3 / 4);
        boxX = x + (width - fullDiameter) / 2;
        boxY = y + (height - fullDiameter) / 2;
        refresh();
    }

    /**
     * Diámetro que le asignó su rueda (el del símbolo "completo").
     */
    int fullSize()
    {
        return fullDiameter;
    }

    /**
     * Diámetro con el que se dibuja el círculo del símbolo. Las
     * subclases lo redefinen cuando cambian el tamaño del símbolo.
     */
    int shownDiameter()
    {
        return fullDiameter;
    }

    /**
     * Evento: el símbolo acaba de quedar en la ventana de su rueda
     * (por un giro o porque se pidió con placeSymbol). El símbolo normal
     * no hace nada.
     */
    void selected()
    {
    }

    /**
     * Evento: la rueda a la que pertenece el símbolo acaba de girar
     * (el símbolo esté o no en la ventana). El símbolo normal no hace
     * nada.
     */
    void spun()
    {
    }

    /**
     * Recalcula tamaño y posición de las figuras y las redibuja si el
     * símbolo está visible. Las subclases la llaman cuando cambia su
     * estado.
     */
    void refresh()
    {
        int size = Math.max(1, shownDiameter());
        int offset = (fullDiameter - size) / 2;

        body.set(boxX + offset, boxY + offset, size);
        if (halo != null)
        {
            int pad = Math.max(1, fullDiameter / 8);
            halo.set(boxX - pad, boxY - pad, fullDiameter + 2 * pad);
        }

        if (drawn)
        {
            repaint();
        }
    }

    /**
     * Dibuja el aro y luego el círculo (para que el aro quede detrás), o
     * los oculta si el símbolo no debe verse.
     */
    private void repaint()
    {
        if (drawn && !isHidden())
        {
            if (halo != null)
            {
                halo.show();
            }
            body.show();
        }
        else
        {
            body.hide();
            if (halo != null)
            {
                halo.hide();
            }
        }
    }

    /**
     * Un círculo de shapes que recuerda su posición y tamaño, porque
     * los movimientos de shapes son relativos.
     */
    private static class Ring
    {
        private Circle circle;
        private int x;
        private int y;
        private int size;

        Ring(String color)
        {
            circle = new Circle();
            x = DEFAULT_X;
            y = DEFAULT_Y;
            size = DEFAULT_DIAMETER;
            circle.changeColor(color);
        }

        void set(int newX, int newY, int newSize)
        {
            if (newSize != size)
            {
                circle.changeSize(newSize);
                size = newSize;
            }
            if (newX != x)
            {
                circle.moveHorizontal(newX - x);
                x = newX;
            }
            if (newY != y)
            {
                circle.moveVertical(newY - y);
                y = newY;
            }
        }

        void show()
        {
            circle.makeVisible();
        }

        void hide()
        {
            circle.makeInvisible();
        }
    }
}
