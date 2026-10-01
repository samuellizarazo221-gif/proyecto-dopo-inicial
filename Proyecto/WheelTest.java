import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

/**
 * Pruebas de unidad de los metodos principales de Wheel.
 * Nunca se llama makeVisible, asi no se abre ninguna ventana.
 */
public class WheelTest
{
    private Wheel wheel;

    /**
     * Antes de cada prueba: una rueda con tres simbolos (red, blue, green).
     * El simbolo actual queda en red.
     */
    @BeforeEach
    public void setUp()
    {
        wheel = new Wheel(1);
        wheel.addSymbol(new Symbol("red"));
        wheel.addSymbol(new Symbol("blue"));
        wheel.addSymbol(new Symbol("green"));
    }

    /**
     * El constructor debe crear una rueda vacia y desbloqueada.
     */
    @Test
    public void shouldCreateEmptyUnlockedWheel()
    {
        Wheel empty = new Wheel(1);
        assertNull(empty.currentSymbol());
        assertEquals(0, empty.getSymbols().size());
        assertFalse(empty.isLocked());
    }

    /**
     * addSymbol debe agregar al final y el primero que entra queda como actual.
     */
    @Test
    public void shouldAddSymbolsInOrder()
    {
        ArrayList<Symbol> symbols = wheel.getSymbols();
        assertEquals(3, symbols.size());
        assertEquals("red", symbols.get(0).getColor());
        assertEquals("blue", symbols.get(1).getColor());
        assertEquals("green", symbols.get(2).getColor());
        assertEquals("red", wheel.currentSymbol().getColor());
    }

    /**
     * currentSymbol debe devolver null si la rueda esta vacia, y si no,
     * el simbolo que esta mostrando.
     */
    @Test
    public void shouldReturnCurrentSymbol()
    {
        assertNull(new Wheel(1).currentSymbol());
        assertEquals("red", wheel.currentSymbol().getColor());
    }

    /**
     * newCurrentSymbol debe avanzar al siguiente simbolo, dar la vuelta
     * al llegar al final y devolver null si la rueda esta vacia.
     */
    @Test
    public void shouldAdvanceToNextSymbolCyclically()
    {
        assertEquals("blue", wheel.newCurrentSymbol().getColor());
        assertEquals("green", wheel.newCurrentSymbol().getColor());
        assertEquals("red", wheel.newCurrentSymbol().getColor());
        assertNull(new Wheel(1).newCurrentSymbol());
    }

    /**
     * spin debe avanzar la cantidad de pasos pedida (con vuelta completa
     * queda igual) y no fallar si la rueda esta vacia.
     */
    @Test
    public void shouldSpinTheGivenSteps()
    {
        wheel.spin(2);
        assertEquals("green", wheel.currentSymbol().getColor());

        wheel.spin(3);
        assertEquals("green", wheel.currentSymbol().getColor());

        wheel.spin(0);
        assertEquals("green", wheel.currentSymbol().getColor());

        Wheel empty = new Wheel(1);
        empty.spin(5);
        assertNull(empty.currentSymbol());
    }

    /**
     * lock debe dejar la rueda bloqueada.
     */
    @Test
    public void shouldLockWheel()
    {
        wheel.lock();
        assertTrue(wheel.isLocked());
    }

    /**
     * unlock debe dejar la rueda desbloqueada otra vez.
     */
    @Test
    public void shouldUnlockWheel()
    {
        wheel.lock();
        wheel.unlock();
        assertFalse(wheel.isLocked());
    }

    /**
     * isLocked debe ser false al inicio y true despues de bloquear.
     */
    @Test
    public void shouldReportIfWheelIsLocked()
    {
        assertFalse(wheel.isLocked());
        wheel.lock();
        assertTrue(wheel.isLocked());
    }

    /**
     * placeSymbol debe poner como actual el simbolo del color pedido y
     * devolver true; si no existe, devuelve false y no cambia nada.
     */
    @Test
    public void shouldPlaceExistingSymbol()
    {
        assertTrue(wheel.placeSymbol("green"));
        assertEquals("green", wheel.currentSymbol().getColor());

        assertFalse(wheel.placeSymbol("pink"));
        assertEquals("green", wheel.currentSymbol().getColor());
    }

    /**
     * delSymbol debe quitar el simbolo del color dado; si era el actual
     * (el ultimo), la rueda vuelve al primero. Si el color no existe,
     * no pasa nada.
     */
    @Test
    public void shouldDeleteSymbolByColor()
    {
        wheel.placeSymbol("green");
        wheel.delSymbol("green");
        assertEquals(2, wheel.getSymbols().size());
        assertEquals("red", wheel.currentSymbol().getColor());

        wheel.delSymbol("pink");
        assertEquals(2, wheel.getSymbols().size());
    }

    /**
     * swapContent debe intercambiar los simbolos y el simbolo actual
     * entre las dos ruedas.
     */
    @Test
    public void shouldSwapContentWithOtherWheel()
    {
        Wheel other = new Wheel(2);
        other.addSymbol(new Symbol("yellow"));

        wheel.swapContent(other);

        assertEquals(1, wheel.getSymbols().size());
        assertEquals("yellow", wheel.currentSymbol().getColor());
        assertEquals(3, other.getSymbols().size());
        assertEquals("red", other.currentSymbol().getColor());
    }

    /**
     * allSymbolMatch debe dar true solo si el simbolo actual tiene el mismo
     * color que el recibido; con null o con rueda vacia da false.
     */
    @Test
    public void shouldCompareCurrentSymbolColor()
    {
        assertTrue(wheel.allSymbolMatch(new Symbol("red")));
        assertFalse(wheel.allSymbolMatch(new Symbol("blue")));
        assertFalse(wheel.allSymbolMatch(null));
        assertFalse(new Wheel(1).allSymbolMatch(new Symbol("red")));
    }

    /**
     * getSymbols debe devolver una copia: si se modifica la lista que
     * devuelve, la rueda no cambia.
     */
    @Test
    public void shouldReturnCopyOfSymbols()
    {
        ArrayList<Symbol> copy = wheel.getSymbols();
        copy.clear();
        assertEquals(3, wheel.getSymbols().size());
    }
}