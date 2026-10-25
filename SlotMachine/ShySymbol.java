/**
 * Símbolo tímido: cada vez que queda seleccionado en la ventana de su
 * rueda alterna entre visible e invisible. Mientras está invisible la
 * rueda no muestra ningún símbolo (la máquina no lo cuenta en su
 * configuración). Se dibuja con un aro violeta.
 */
public class ShySymbol extends Symbol
{
    private static final String HALO_COLOR = "mediumpurple";

    private boolean hidden;

    /**
     * Crea un símbolo tímido del color indicado, inicialmente visible.
     *
     * @param color nombre CSS o valor "#rrggbb"
     */
    public ShySymbol(String color)
    {
        super(color, HALO_COLOR);
        hidden = false;
    }

    /**
     * Tipo de este símbolo.
     *
     * @return "shy"
     */
    @Override
    public String getType()
    {
        return "shy";
    }

    /**
     * Indica si el símbolo está escondido en este momento.
     *
     * @return true mientras esté escondido
     */
    @Override
    public boolean isHidden()
    {
        return hidden;
    }

    /**
     * El símbolo quedó seleccionado: alterna su estado de visible a
     * invisible, o al revés.
     */
    @Override
    void selected()
    {
        hidden = !hidden;
        refresh();
    }
}
