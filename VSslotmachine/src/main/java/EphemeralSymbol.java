/**
 * Símbolo efímero: con cada giro de su rueda se va achicando hasta
 * quedar convertido en un punto. Se dibuja con un aro naranja; dentro
 * del aro se ve cuánto le queda de vida.
 */
public class EphemeralSymbol extends Symbol
{
    private static final String HALO_COLOR = "orange";
    private static final int LIFE = 10;

    private int spins;

    /**
     * Crea un símbolo efímero del color indicado, con su tamaño completo.
     *
     * @param color nombre CSS o valor "#rrggbb"
     */
    public EphemeralSymbol(String color)
    {
        super(color, HALO_COLOR);
        spins = 0;
    }

    /**
     * Tipo de este símbolo.
     *
     * @return "ephemeral"
     */
    @Override
    public String getType()
    {
        return "ephemeral";
    }

    /**
     * Indica si el símbolo ya quedó convertido en un punto.
     *
     * @return true si ya no puede achicarse más
     */
    public boolean isPoint()
    {
        return getDiameter() <= 1;
    }

    /**
     * Cada giro le quita una décima parte de su tamaño completo; a los
     * diez giros es un punto (diámetro 1) y ahí se queda.
     */
    @Override
    int shownDiameter()
    {
        return fullSize() * (LIFE - Math.min(spins, LIFE)) / LIFE;
    }

    /**
     * La rueda giró: el símbolo se achica un paso.
     */
    @Override
    void spun()
    {
        if (spins < LIFE)
        {
            spins++;
            refresh();
        }
    }
}
