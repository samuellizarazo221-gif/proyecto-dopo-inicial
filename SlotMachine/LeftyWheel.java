/**
 * Rueda zurda: si hay una rueda a su izquierda, al girar copia su estado
 * (muestra el símbolo del mismo color que esa rueda). Si no hay rueda a
 * su izquierda, o si no tiene un símbolo de ese color, gira como una
 * rueda normal. Se reconoce por su ventana azul claro.
 */
public class LeftyWheel extends Wheel
{
    /**
     * Crea una rueda zurda vacía con la geometría por defecto.
     */
    public LeftyWheel()
    {
        super();
        applyIdleColor();
    }

    /**
     * Nombre del tipo de esta rueda.
     *
     * @return "lefty"
     */
    @Override
    public String type()
    {
        return "lefty";
    }

    /**
     * Copia el símbolo que muestra la rueda de la izquierda.
     *
     * @param left la rueda que está a su izquierda (null si no hay)
     * @return true si pudo copiarla; false si debe girar normalmente
     */
    @Override
    boolean follow(Wheel left)
    {
        Symbol seen = (left == null) ? null : left.currentSymbol();

        if (seen == null || !hasSymbol(seen.getColor()))
        {
            return false;
        }

        jumpTo(seen.getColor());
        return true;
    }

    /**
     * Ventana azul claro.
     */
    @Override
    protected String idleColor()
    {
        return "lightskyblue";
    }
}
