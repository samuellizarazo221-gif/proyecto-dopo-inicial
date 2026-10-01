import java.util.ArrayList;

/**
 * Resuelve y simula el problema de la maratón (ICPC WF 2025,
 * Problem I: Slot Machine) usando SlotMachine únicamente como
 * "herramienta de prueba" ciega: solve() jamás lee los colores
 * reales, solo usa spin(wheel,steps) y distinctSymbols().
 */
public class SlotMachineContest
{
    private static SlotMachine lastSolved;

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

        
        for (int wheel = 2; wheel <= n; wheel++)
        {
            int bestStep = 0;
            int bestDistinct = -1;

            for (int step = 1; step <= n; step++)
            {
                sm.spin(wheel, 1);
                actions.add(new int[] { wheel, 1 });

                int distinct = sm.distinctSymbols();
                if (distinct > bestDistinct)
                {
                    bestDistinct = distinct;
                    bestStep = step;
                }
            }

            sm.spin(wheel, bestStep);
            actions.add(new int[] { wheel, bestStep });
        }

        
        boolean[] identified = new boolean[n + 1];
        int[] relativeOffset = new int[n + 1];
        int remaining = n - 1;
        int t = 0;

        while (remaining > 0)
        {
            t++;
            sm.spin(1, 1);
            actions.add(new int[] { 1, 1 });

            for (int candidate = 2; candidate <= n; candidate++)
            {
                if (identified[candidate])
                {
                    continue;
                }

                sm.spin(candidate, -1);
                actions.add(new int[] { candidate, -1 });

                if (sm.distinctSymbols() == n)
                {
                    identified[candidate] = true;
                    relativeOffset[candidate] = t;
                    remaining--;
                    break;
                }
                else
                {
                    sm.spin(candidate, 1);
                    actions.add(new int[] { candidate, 1 });
                }
            }
        }

        
        sm.spin(1, -(n - 1));
        actions.add(new int[] { 1, -(n - 1) });

        for (int wheel = 2; wheel <= n; wheel++)
        {
            int delta = 1 - relativeOffset[wheel];
            sm.spin(wheel, delta);
            actions.add(new int[] { wheel, delta });
        }

        lastSolved = sm;
        return actions.toArray(new int[0][]);
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