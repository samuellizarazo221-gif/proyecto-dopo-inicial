import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de unidad  Symbol, una por cada metodo.
 */
public class SymbolTest
{
    /**
     * El constructor debe crear el simbolo con el color que le pasamos.
     */
    @Test
    public void shouldCreateSymbolWithGivenColor()
    {
        Symbol symbol = new Symbol("red");
        assertNotNull(symbol);
        assertEquals("red", symbol.getColor());
    }

    /**
     * getColor debe devolver el color de cada simbolo, sin mezclarlos.
     */
    @Test
    public void shouldReturnOwnColor()
    {
        Symbol a = new Symbol("red");
        Symbol b = new Symbol("#00ff00");
        assertEquals("red", a.getColor());
        assertEquals("#00ff00", b.getColor());
    }

    /**
     * moveHorizontal no debe fallar con valores positivos, negativos ni cero.
     * (Symbol no dice donde esta, asi que solo revisamos que no se dane.)
     */
    @Test
    public void shouldMoveHorizontally()
    {
        Symbol symbol = new Symbol("blue");
        symbol.moveHorizontal(50);
        symbol.moveHorizontal(-20);
        symbol.moveHorizontal(0);
        assertEquals("blue", symbol.getColor());
    }

    /**
     * setPosition debe poder ubicar el simbolo sin fallar.
     */
    @Test
    public void shouldSetPosition()
    {
        Symbol symbol = new Symbol("blue");
        symbol.setPosition(100, 80);
        assertEquals("blue", symbol.getColor());
    }

    /**
     * makeVisible debe poder llamarse (incluso dos veces) sin fallar.
     */
    @Test
    public void shouldMakeVisible()
    {
        Symbol symbol = new Symbol("green");
        symbol.makeVisible();
        symbol.makeVisible();
        assertEquals("green", symbol.getColor());
        symbol.makeInvisible();
    }

    /**
     * makeInvisible debe poder llamarse, este visible o no, sin fallar.
     */
    @Test
    public void shouldMakeInvisible()
    {
        Symbol symbol = new Symbol("green");
        symbol.makeInvisible();
        symbol.makeVisible();
        symbol.makeInvisible();
        assertEquals("green", symbol.getColor());
    }

    /**
     * getDistinctSymbols debe contar los colores distintos, sin contar
     * los repetidos ni los nulos.
     */
    @Test
    public void shouldCountDistinctColors()
    {
        assertEquals(0, Symbol.getDistinctSymbols(new String[0]));
        assertEquals(1, Symbol.getDistinctSymbols(new String[] {"red"}));
        assertEquals(4, Symbol.getDistinctSymbols(new String[] {"red", "blue", "green", "yellow"}));
        assertEquals(3, Symbol.getDistinctSymbols(new String[] {"red", "blue", "red", "blue", "green"}));
        assertEquals(2, Symbol.getDistinctSymbols(new String[] {"red", null, "blue", null}));
        assertEquals(0, Symbol.getDistinctSymbols(new String[] {null, null}));
    }
}