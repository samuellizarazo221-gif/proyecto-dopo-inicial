import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas de unidad de Symbol (siempre en modo invisible).
 */
public class SymbolTest
{
    /**
     * Debería guardar el color sin espacios y en minúsculas.
     */
    @Test
    public void shouldNormalizeColor()
    {
        assertEquals("red", new Symbol("  Red ").getColor());
        assertEquals("#00ff00", new Symbol("#00FF00").getColor());
        assertNull(Symbol.normalize(null));
    }

    /**
     * Debería poder ubicarse y redimensionarse varias veces sin fallar.
     */
    @Test
    public void shouldMoveAndResizeWhileInvisible()
    {
        Symbol symbol = new Symbol("blue");
        symbol.setPosition(100, 80);
        symbol.setPosition(10, 10);
        symbol.setDiameter(12);
        assertEquals(12, symbol.getDiameter());
        symbol.setDiameter(0);
        assertEquals(1, symbol.getDiameter());
    }

    /**
     * Debería conservar el color con el que fue creado.
     */
    @Test
    public void shouldContainTheColorItWasCreatedWith()
    {
        assertEquals("green", new Symbol("green").getColor());
    }

    /**
     * Debería contar los colores distintos ignorando los nulos.
     */
    @Test
    public void shouldCountDistinctColorsIgnoringNulls()
    {
        assertEquals(0, Symbol.getDistinctSymbols(new String[0]));
        assertEquals(0, Symbol.getDistinctSymbols(new String[] {null, null}));
        assertEquals(2, Symbol.getDistinctSymbols(new String[] {"red", null, "blue", "red"}));
    }

    /**
     * Debería crear cada tipo de símbolo por su nombre y no crear los
     * que no existen.
     */
    @Test
    public void shouldCreateEverySymbolType()
    {
        assertEquals("normal", Symbol.create("normal", "red").getType());
        assertEquals("ephemeral", Symbol.create("Ephemeral", "red").getType());
        assertEquals("shy", Symbol.create(" SHY ", " Red ").getType());
        assertEquals("red", Symbol.create("shy", " Red ").getColor());
        assertNull(Symbol.create("giant", "red"));
        assertNull(Symbol.create(null, "red"));
    }

    /**
     * Un símbolo efímero debería achicarse con cada giro hasta quedar
     * como un punto, y ahí quedarse.
     */
    @Test
    public void shouldShrinkEphemeralSymbolUntilItIsAPoint()
    {
        EphemeralSymbol symbol = new EphemeralSymbol("red");
        symbol.setDiameter(40);
        assertEquals(40, symbol.getDiameter());
        assertFalse(symbol.isPoint());
        symbol.spun();
        assertEquals(36, symbol.getDiameter());
        for (int i = 0; i < 9; i++)
        {
            symbol.spun();
        }
        assertEquals(1, symbol.getDiameter());
        assertTrue(symbol.isPoint());
        symbol.spun();
        assertEquals(1, symbol.getDiameter());
    }

    /**
     * Un símbolo tímido debería alternar entre oculto y visible cada vez
     * que queda seleccionado, y empezar visible.
     */
    @Test
    public void shouldAlternateShySymbolEachTimeItIsSelected()
    {
        ShySymbol symbol = new ShySymbol("blue");
        assertFalse(symbol.isHidden());
        symbol.selected();
        assertTrue(symbol.isHidden());
        symbol.selected();
        assertFalse(symbol.isHidden());
    }

    /**
     * Un símbolo normal no debería cambiar por los eventos de su rueda.
     */
    @Test
    public void shouldNotChangeNormalSymbolWhenWheelSpinsOrSelectsIt()
    {
        Symbol symbol = new Symbol("red");
        symbol.setDiameter(20);
        symbol.spun();
        symbol.selected();
        assertFalse(symbol.isHidden());
        assertEquals(20, symbol.getDiameter());
        assertEquals("normal", symbol.getType());
    }
}
