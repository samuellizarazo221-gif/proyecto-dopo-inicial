import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas de unidad del ciclo 3: constructor SlotMachine(n) y solución
 * del problema de la maratón (siempre en modo invisible).
 */
public class SlotMachineContestTest
{
    /**
     * Debería crear una máquina con n ruedas y n símbolos distintos en
     * cada rueda, todas con los símbolos en el mismo orden.
     */
    @Test
    public void shouldCreateMachineWithSameNumberOfWheelsAndSymbols()
    {
        int n = 7;
        SlotMachine machine = new SlotMachine(n);
        machine.makeInvisible();
        String[] symbols = machine.symbols();

        assertEquals(n, machine.configuration().length);
        assertEquals(n * n, symbols.length);
        assertEquals(n, Symbol.getDistinctSymbols(symbols));
        for (int i = 0; i < n * n; i++)
        {
            assertEquals(symbols[i % n], symbols[i]);
        }
    }

    /**
     * Debería crear una máquina que no empieza ganando.
     */
    @Test
    public void shouldCreateMachineThatIsNotAWinner()
    {
        for (int i = 0; i < 50; i++)
        {
            SlotMachine machine = new SlotMachine(3);
            machine.makeInvisible();
            assertFalse(machine.isJackpot());
            assertTrue(machine.distinctSymbols() > 1);
        }
    }

    /**
     * Debería crear símbolos de colores todos distintos.
     */
    @Test
    public void shouldCreateDifferentColorsForEverySymbol()
    {
        SlotMachine machine = new SlotMachine(50);
        machine.makeInvisible();
        java.util.HashSet<String> colors = new java.util.HashSet<String>();

        for (int i = 0; i < 50; i++)
        {
            colors.add(machine.symbols()[i]);
        }
        assertEquals(50, colors.size());
    }

    /**
     * No debería resolver tamaños fuera del rango del problema.
     */
    @Test
    public void shouldNotSolveOutOfRangeSizes()
    {
        assertEquals(0, SlotMachineContest.solve(2).length);
        assertEquals(0, SlotMachineContest.solve(0).length);
        assertEquals(0, SlotMachineContest.solve(51).length);
        SlotMachineContest.simulate(2);
    }

    /**
     * Debería devolver acciones válidas: a lo sumo una por rueda, con
     * rueda existente y giro distinto de cero.
     */
    @Test
    public void shouldReturnValidActions()
    {
        for (int n = 3; n <= 50; n++)
        {
            int[][] actions = SlotMachineContest.solve(n);
            assertTrue(actions.length > 0 && actions.length <= n);
            for (int[] action : actions)
            {
                assertTrue(action[0] >= 1 && action[0] <= n);
                assertTrue(action[1] != 0 && Math.abs(action[1]) <= n / 2);
            }
        }
    }

    /**
     * Debería dejar ganadora cualquier máquina de 3 a 50 ruedas.
     */
    @Test
    public void shouldSolveEverySize()
    {
        for (int n = 3; n <= 50; n++)
        {
            SlotMachine machine = new SlotMachine(n);
            machine.makeInvisible();
            SlotMachineContest.solve(machine, n);
            assertTrue("n = " + n, machine.isJackpot());
        }
    }

    /**
     * Debería llevar la máquina a ganar al repetir las acciones que
     * devolvió desde la configuración inicial.
     */
    @Test
    public void shouldWinWhenReplayingTheReturnedActions()
    {
        SlotMachine machine = new SlotMachine(20);
        machine.makeInvisible();
        String[] start = machine.configuration();
        int[][] actions = SlotMachineContest.solve(machine, 20);

        for (int[] action : actions)
        {
            machine.spin(action[0], -action[1]);
        }
        assertArrayEquals(start, machine.configuration());
        assertFalse(machine.isJackpot());

        for (int[] action : actions)
        {
            machine.spin(action[0], action[1]);
        }
        assertTrue(machine.isJackpot());
    }

    /**
     * Debería resolver configuraciones difíciles: todas las ruedas
     * distintas y configuraciones con simetría.
     */
    @Test
    public void shouldSolveSymmetricConfigurations()
    {
        int n = 12;
        for (int period : new int[] {1, 2, 3, 4, 6})
        {
            SlotMachine machine = new SlotMachine(n);
            machine.makeInvisible();
            String[] symbols = machine.symbols();
            String[] target = new String[n];
            for (int i = 0; i < n; i++)
            {
                target[i] = symbols[(i % period) * (n / period) % n];
            }
            machine.spin(target);
            SlotMachineContest.solve(machine, n);
            assertTrue("period " + period, machine.isJackpot());
        }
    }
}
