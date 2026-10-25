import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas de unidad compartidas del ciclo 3 (creación colectiva). El
 * nombre de cada prueba incluye las iniciales de sus autores.
 */
public class SlotMachineContestCTest
{
    /**
     * Debería resolver la máquina más pequeña del problema.
     */
    @Test
    public void accordingBoLiShouldSolveSmallestMachine()
    {
        for (int i = 0; i < 100; i++)
        {
            SlotMachine machine = new SlotMachine(3);
            machine.makeInvisible();
            SlotMachineContest.solve(machine, 3);
            assertTrue(machine.isJackpot());
        }
    }

    /**
     * Debería resolver la máquina más grande del problema sin usar más
     * de una acción neta por rueda.
     */
    @Test
    public void accordingBoLiShouldSolveLargestMachineWithAtMostOneActionPerWheel()
    {
        SlotMachine machine = new SlotMachine(50);
        machine.makeInvisible();
        int[][] actions = SlotMachineContest.solve(machine, 50);

        assertTrue(machine.isJackpot());
        assertTrue(actions.length <= 50);
    }
}
