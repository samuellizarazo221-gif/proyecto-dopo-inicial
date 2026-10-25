import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad de Wheel (siempre en modo invisible).
 */
public class WheelTest
{
    private Wheel wheel;

    /**
     * Prepara una rueda con red, blue y green (muestra red).
     */
    @Before
    public void setUp()
    {
        wheel = new Wheel();
        wheel.addSymbol(new Symbol("red"));
        wheel.addSymbol(new Symbol("blue"));
        wheel.addSymbol(new Symbol("green"));
    }

    /**
     * Una rueda vacía no debería tener símbolo actual ni poder girar.
     */
    @Test
    public void shouldHaveNoCurrentSymbolWhenEmpty()
    {
        Wheel empty = new Wheel();
        assertNull(empty.currentSymbol());
        assertNull(empty.newCurrentSymbol());
        empty.spin(5);
        assertNull(empty.currentSymbol());
    }

    /**
     * Debería avanzar de forma cíclica.
     */
    @Test
    public void shouldAdvanceCyclically()
    {
        assertEquals("blue", wheel.newCurrentSymbol().getColor());
        assertEquals("green", wheel.newCurrentSymbol().getColor());
        assertEquals("red", wheel.newCurrentSymbol().getColor());
    }

    /**
     * Debería girar con pasos positivos, negativos y mayores que la
     * cantidad de símbolos.
     */
    @Test
    public void shouldSpinAnyNumberOfSteps()
    {
        wheel.spin(4);
        assertEquals("blue", wheel.currentSymbol().getColor());
        wheel.spin(-2);
        assertEquals("green", wheel.currentSymbol().getColor());
        wheel.spin(-1000000000);
        assertEquals("blue", wheel.currentSymbol().getColor());
    }

    /**
     * Debería colocar un símbolo existente y rechazar uno que no existe.
     */
    @Test
    public void shouldPlaceOnlyExistingSymbols()
    {
        assertTrue(wheel.placeSymbol("green"));
        assertEquals("green", wheel.currentSymbol().getColor());
        assertFalse(wheel.placeSymbol("pink"));
        assertEquals("green", wheel.currentSymbol().getColor());
    }

    /**
     * Debería mantener el símbolo que se ve cuando se elimina otro
     * anterior, y pasar al siguiente cuando se elimina el que se ve.
     */
    @Test
    public void shouldKeepTrackOfTheCurrentSymbolWhenDeleting()
    {
        wheel.placeSymbol("green");
        assertTrue(wheel.delSymbol("red"));
        assertEquals("green", wheel.currentSymbol().getColor());
        assertTrue(wheel.delSymbol("green"));
        assertEquals("blue", wheel.currentSymbol().getColor());
        assertFalse(wheel.delSymbol("red"));
    }

    /**
     * Debería quedar bloqueada y desbloqueada según se pida.
     */
    @Test
    public void shouldLockAndUnlock()
    {
        assertFalse(wheel.isLocked());
        wheel.lock();
        assertTrue(wheel.isLocked());
        wheel.unlock();
        assertFalse(wheel.isLocked());
    }

    /**
     * Debería intercambiar el contenido con otra rueda.
     */
    @Test
    public void shouldSwapContentWithAnotherWheel()
    {
        Wheel other = new Wheel();
        other.addSymbol(new Symbol("yellow"));
        wheel.swapContent(other);
        assertEquals("yellow", wheel.currentSymbol().getColor());
        assertEquals("red", other.currentSymbol().getColor());
        assertEquals(1, wheel.getSymbols().size());
        assertEquals(3, other.getSymbols().size());
    }

    /**
     * Debería comparar su símbolo actual con otro por color.
     */
    @Test
    public void shouldMatchSymbolsByColor()
    {
        assertTrue(wheel.allSymbolMatch(new Symbol("red")));
        assertFalse(wheel.allSymbolMatch(new Symbol("blue")));
        assertFalse(wheel.allSymbolMatch(null));
        assertFalse(new Wheel().allSymbolMatch(new Symbol("red")));
    }

    /**
     * Una rueda reverse debería girar hacia atrás.
     */
    @Test
    public void shouldSpinReverseWheelBackwards()
    {
        Wheel reverse = new ReverseWheel();
        reverse.addSymbol(new Symbol("red"));
        reverse.addSymbol(new Symbol("blue"));
        reverse.addSymbol(new Symbol("green"));
        reverse.spin(1);
        assertEquals("green", reverse.currentSymbol().getColor());
        assertEquals("blue", reverse.newCurrentSymbol().getColor());
        assertEquals("reverse", reverse.type());
    }

    /**
     * Una rueda rebel no debería dejarse bloquear, intercambiar ni
     * eliminar; una normal sí.
     */
    @Test
    public void shouldRefuseLockSwapAndRemovalOnRebelWheel()
    {
        Wheel rebel = new RebelWheel();
        rebel.lock();
        assertFalse(rebel.isLocked());
        assertFalse(rebel.isLockable());
        assertFalse(rebel.isSwappable());
        assertFalse(rebel.isRemovable());
        assertTrue(wheel.isLockable() && wheel.isSwappable() && wheel.isRemovable());
    }

    /**
     * Una rueda lefty debería copiar el símbolo de su vecina si lo tiene
     * y no copiar nada si no lo tiene.
     */
    @Test
    public void shouldCopyLeftNeighbourOnLeftyWheel()
    {
        Wheel lefty = new LeftyWheel();
        lefty.addSymbol(new Symbol("green"));
        lefty.addSymbol(new Symbol("red"));
        assertTrue(lefty.follow(wheel));
        assertEquals("red", lefty.currentSymbol().getColor());
        wheel.spin(1);
        assertTrue(lefty.follow(wheel) == false);
        assertFalse(lefty.follow(null));
        assertEquals("red", lefty.currentSymbol().getColor());
    }

    /**
     * La rueda no debería mostrar un símbolo tímido mientras esté oculto.
     */
    @Test
    public void shouldNotShowShySymbolWhileHidden()
    {
        Wheel shy = new Wheel();
        shy.addSymbol(new Symbol("red"));
        shy.addSymbol(new ShySymbol("blue"));
        shy.spin(1);
        assertEquals("blue", shy.currentSymbol().getColor());
        assertNull(shy.shownSymbol());
        shy.spin(1);
        assertEquals("red", shy.shownSymbol().getColor());
        shy.spin(1);
        assertEquals("blue", shy.shownSymbol().getColor());
    }

    /**
     * Los símbolos efímeros deberían achicarse con cada giro de la rueda
     * pero no al colocarlos.
     */
    @Test
    public void shouldShrinkEphemeralSymbolOnEverySpinButNotWhenPlaced()
    {
        Wheel ephemeral = new Wheel();
        Symbol symbol = new EphemeralSymbol("red");
        ephemeral.addSymbol(symbol);
        ephemeral.addSymbol(new Symbol("green"));
        int full = symbol.getDiameter();
        ephemeral.placeSymbol("green");
        ephemeral.placeSymbol("red");
        assertEquals(full, symbol.getDiameter());
        ephemeral.spin(1);
        assertTrue(symbol.getDiameter() < full);
        for (int i = 0; i < 12; i++)
        {
            ephemeral.spin(1);
        }
        assertEquals(1, symbol.getDiameter());
    }
}
