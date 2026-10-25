import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad compartidas del ciclo 4 (creación colectiva). El
 * nombre de cada prueba incluye las iniciales de sus autores.
 */
public class SlotMachineCC4Test
{
    private SlotMachine machine;

    /**
     * Prepara una máquina vacía e invisible.
     */
    @Before
    public void setUp()
    {
        machine = new SlotMachine();
        machine.makeInvisible();
    }

    /**
     * Una rueda lefty debería quedar igual a la rueda de su izquierda
     * después de girar.
     */
    @Test
    public void accordingBoLiShouldMakeLeftyWheelCopyItsLeftNeighbour()
    {
        machine.addWheel("normal", 1);
        machine.addWheel("lefty", 2);
        String[] colors = {"red", "green", "blue"};
        for (String color : colors)
        {
            machine.addSymbol(1, color);
            machine.addSymbol(2, color);
        }
        machine.spin(1, 2);
        machine.spin(2, 1);
        assertEquals("blue", machine.configuration()[0]);
        assertArrayEquals(new String[] {"blue", "blue"}, machine.configuration());
    }

    /**
     * Una rueda rebel no debería poder bloquearse, intercambiarse ni
     * eliminarse.
     */
    @Test
    public void accordingBoLiShouldNotLetRebelWheelBeLockedSwappedOrDeleted()
    {
        machine.addWheel("normal", 1);
        machine.addWheel("rebel", 2);
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.lock(2);
        assertFalse(machine.ok());
        machine.swap(1, 2);
        assertFalse(machine.ok());
        machine.delWheel(2);
        assertFalse(machine.ok());
        assertArrayEquals(new String[] {"red", "blue"}, machine.configuration());
    }

    /**
     * No debería aceptar tipos de rueda ni de símbolo que no existen.
     */
    @Test
    public void accordingBoLiShouldRejectUnknownWheelAndSymbolTypes()
    {
        machine.addWheel("giant", 1);
        assertFalse(machine.ok());
        machine.addWheel("normal", 1);
        machine.addSymbol("giant", 1, "red");
        assertFalse(machine.ok());
        assertEquals(1, machine.configuration().length);
        assertEquals(0, machine.symbols().length);
    }
}
