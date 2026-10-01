import java.util.ArrayList;

/**
 * Resuelve y simula el problema de la maratón (ICPC WF 2025,
 * Problem I: Slot Machine) usando SlotMachine únicamente como
 * "herramienta de prueba" ciega: solve() jamás lee los colores
 * reales, solo usa spin(wheel,steps) y distinctSymbols().
 */
public class SlotMachineContest
{


    /**
     * Crea una SlotMachine(n) invisible y calcula la secuencia de
     * acciones {wheel, steps} que la lleva al jackpot, sin superar
     * las 10 000 acciones. Devuelve esa secuencia.
     */
    public static int[][] solve(int n)
    {
        SlotMachine sm = new SlotMachine(n);
        sm.makeInvisible();

        ArrayList<int[]> actions = new ArrayList<int[]>();

    }   

    /**
     * Resuelve una máquina de tamaño n y la muestra ya en su
     * configuración ganadora.
     */
    public static void simulate(int n)
    {
        solve(n);
        lastSolved.makeVisible();
    }
}