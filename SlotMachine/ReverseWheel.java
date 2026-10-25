/**
 * Rueda inversa (tipo propuesto por el equipo): gira al contrario que
 * las demás. Un giro de k pasos la mueve k símbolos hacia atrás. Se
 * reconoce por su ventana color ciruela.
 */
public class ReverseWheel extends Wheel
{
    /**
     * Crea una rueda inversa vacía con la geometría por defecto.
     */
    public ReverseWheel()
    {
        super();
        applyIdleColor();
    }

    /**
     * Nombre del tipo de esta rueda.
     *
     * @return "reverse"
     */
    @Override
    public String type()
    {
        return "reverse";
    }

    /**
     * @return -1: gira hacia atrás
     */
    @Override
    protected int direction()
    {
        return -1;
    }

    /**
     * Ventana color ciruela.
     */
    @Override
    protected String idleColor()
    {
        return "plum";
    }
}
