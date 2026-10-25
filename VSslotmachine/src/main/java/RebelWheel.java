/**
 * Rueda rebelde: no se deja bloquear, ni intercambiar, ni eliminar. Se
 * reconoce por su ventana color salmón.
 */
public class RebelWheel extends Wheel
{
    /**
     * Crea una rueda rebelde vacía con la geometría por defecto.
     */
    public RebelWheel()
    {
        super();
        applyIdleColor();
    }

    /**
     * Nombre del tipo de esta rueda.
     *
     * @return "rebel"
     */
    @Override
    public String type()
    {
        return "rebel";
    }

    /**
     * Una rueda rebelde ignora el bloqueo.
     */
    @Override
    public void lock()
    {
    }

    /**
     * @return false: no se deja bloquear
     */
    @Override
    public boolean isLockable()
    {
        return false;
    }

    /**
     * @return false: no se deja intercambiar
     */
    @Override
    public boolean isSwappable()
    {
        return false;
    }

    /**
     * @return false: no se deja eliminar
     */
    @Override
    public boolean isRemovable()
    {
        return false;
    }

    /**
     * Ventana color salmón.
     */
    @Override
    protected String idleColor()
    {
        return "lightsalmon";
    }
}
