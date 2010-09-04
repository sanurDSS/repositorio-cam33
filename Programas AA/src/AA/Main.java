package AA;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;

/**
 *
 */
public class Main
{

	/**
	 * @param args Argumentos de ejecución de la línea de comandos.
	 */
	public static void main(String[] args)
	{
		
		/*{
			System.out.println("P01 - Problema de la barra ");
			System.out.println("P01: ");

			String data2 = "0";
			for (int i = 0; i < 170; i++)
				data2 += " " + i;
			for (int i = 179; i < 1000; i=i+7)
				data2 += " " + i;

			long tiempoInicial = System.currentTimeMillis();
			long tiempoActual = 0;
			long tiempoMedioLlamada = 0;
			for (int i = 0; i < 100; i++)
			{
				tiempoActual = System.currentTimeMillis();
				P01.best(data2);
				tiempoMedioLlamada += System.currentTimeMillis() - tiempoActual;
			}
			System.out.println(tiempoMedioLlamada/(100*1000.0));
			System.out.println((System.currentTimeMillis()-tiempoInicial)/1000.0);
		}*/

		/*{
			System.out.println("P01 - Problema de la barra ");
			System.out.println("P01: ");

			String data2 = "0";
			for (int i = 0; i < 170; i++)
				data2 += " " + i;
			for (int i = 179; i < 1000; i=i+7)
				data2 += " " + i;

			long tiempoInicial = System.currentTimeMillis();
			long tiempoActual = 0;
			long tiempoMedioLlamada = 0;
			for (int i = 0; i < 100; i++)
			{
				tiempoActual = System.currentTimeMillis();
				P01.best(data2);
				tiempoMedioLlamada += System.currentTimeMillis() - tiempoActual;
			}
			System.out.println(tiempoMedioLlamada/(100*1000.0));
			System.out.println((System.currentTimeMillis()-tiempoInicial)/1000.0);
		}*/

		/*{
			P02 p = new P02();
			String[] data = {"72 44 31", "17 23 24", "26"};
			System.out.println(p.bestChoice(data));

			String[] data2 = {"64 85 52 99 39 54", "26 22 4 28 13 9", "23 20 20 26"};
			System.out.println(p.bestChoice(data2));

		}*/
		
		/*{
			System.out.println("P03 - Problema de las tareas y las 2 máquinas");
			String[] data = {"4 4 3 5", "2 3 4 4"};
			Integer[] salida = P03.bestSolution(data);

			System.out.println();
			String[] data2 = {"4 4 3 5 8 10 39 56 38 24 55 12 1 2 3 4 1 23 4 10 79 78 23 12 54 12 3 4 9", "14 3 2 5 18 11 59 53 38 24 55 12 14 54 12 7 14 9 1 2 3 4 1 23 4 10 79 78 23"};
			P03.bestSolution(data2);

			System.out.println();
			String[] data3 = {"23 44 5 6 8 55 4 4 3 5 8 6 7 5 10 10 26 67 39 56 38 24 55 12 1 2 3 4 1 23 4 10 79 78 23 12 54 12 3 4 9", "21 40 10 10 9 50 14 3 2 5 18 44 11 59 53 38 24 34 55 12 1 14 54 2 5 9 12 7 14 9 1 2 3 4 1 23 4 10 79 78 23"};
			P03.bestSolution(data3);
			System.out.println();
			for (int i = 0; i < 20; i++)
			{
				data[0] += " " + (int) (Math.random()*100);
				data[1] += " " + (int) (Math.random()*100);
			}
			P03.bestSolution(data);
		}*/

		/*{
			System.out.println("P91 - Problema de la mochila");
			String data[] = {"10 10 10", "15 12 13", "25"};
			System.out.println(P91.best(data));
		}*/

		{
			//P92 p = new P92();
			//System.out.println(p.best("22 1 3 9 27"));
			//System.out.println(p.best("44 1 3 9 27 24 16 30 70 38 24 7 13 7 1"));
			//System.out.println(p.best("4540 1 3 9 27 24 16 30 70 38 24 7 13 7 1 2000 1000 1200 231"));
			//System.out.println(p.best("45400 1 30 9 27 24 16 30 70 38 24 7 13 7 1 4 5 1 2 3 0 1 2 120 2310 231 23 123 42 12 5 6 5 4 5 6 4 6 4 20000 10000 1200 231"));
		}

		{
			//P93 p = new P93();
			//ArrayList<Integer> solucion = p.bestSolution("22 1 3 9 27");
			//System.out.println(solucion.toString());
			//System.out.println("····································");
			//System.out.println(p.bestSolution("44 1 3 9 27 24 16 30 70 38 24 7 13 7 1").toString());


			//System.out.println(p.bestSolution("274 2 8 6 39 67 92 36 78 53 6 5 6 8 9 6 5 9 3 8 9 7 4 2 3 82 9 73 4 75 8 9 2 3 4 7 8 9 2 3 4 9 7 8 4 2 3 9 7 8 3 4 9 7 8 4 2 3"));
		}

		/*{
			P05 p = new P05();
			String[] data =
			{"1100000",
			"0101000",
			"0010100",
			"0001000",
			"0000101",
			"0000010",
			"0000001"};

			System.out.println(p.best(data));
			System.out.println("---------------------");

			String[] data2 =
			{"100010000",
			 "010000000",
			 "001000010",
			 "000100000",
			 "000010000",
			 "000001000",
			 "000000100",
			 "000000010",
			 "000000001"};

			for (int i = 0; i < data2.length; i++)
			{
				System.out.println(data2[i]);
			}
			System.out.println("Resultado: " + p.best(data2));
			System.out.println("---------------------");
		}*/

		/*{
			P503 p = new P503();
			String[] data1 = {"5 3 6 3", "6 6 8 2"};
			System.out.println("Resultado: " + p.best(data1));
			String[] data2 = {"4 7 1 1 4 7","3 8 4 2 8 5"};
			System.out.println("Resultado: " + p.best(data2));
			String[] data3 = {"1 2 3 4 5 6 7","1 4 9 16 25 36 49"};
			System.out.println("Resultado: " + p.best(data3));
			String[] data4 = {"1 2 3 4 5 6 7 8 10 22 11 2 30 12 33","1 4 9 16 25 36 49 54 5 1 12 11 12 40 20983"};
			System.out.println("Resultado: " + p.best(data4));

			long tiempoTotal = 0;
			int ejecuciones = 100000;
			for (int j = 0; j < ejecuciones; j++)
			{
				String[] data = {"",""};
				for (int i = 1; i < 10; i++)
				{
					data[0] += (int) ((Math.random()*100))%20 + " ";
					data[1] += (int) ((Math.random()*100))%20 + " ";
				}
				data[0] += (int) ((Math.random()*100))%100;
				data[1] += (int) ((Math.random()*100))%100;

				long tiempoActual = System.currentTimeMillis();
				p.best(data);
				tiempoTotal += System.currentTimeMillis() - tiempoActual;
			}
			System.out.println(tiempoTotal/1000.0f/ejecuciones + " segundos");
		}*/

		/*{
			P94 p = new P94();
			String[] data1 = {"0 1", "1 2", "2 3"};
			System.out.println("Resultado: " + p.best(data1));
			System.out.println();
			String[] data2 = {"0 1", "0 2", "0 3", "0 4"};
			System.out.println("Resultado: " + p.best(data2));
			System.out.println();

			long tiempoTotal = 0;
			int ejecuciones = 10000;
			for (int j = 0; j < ejecuciones; j++)
			{
				int numCalles = 1000;
				String[] data = new String[numCalles];

				for (int i = 0; i < numCalles; i++)
				{
					data[i] = new String("");
					data[i] += (int) ((Math.random()*100000000))%((numCalles/3)*(numCalles/3)) + " ";
					data[i] += " ";
					data[i] += (int) ((Math.random()*100000000))%((numCalles/3)*(numCalles/3)) + " ";
				}


				long tiempoActual = System.currentTimeMillis();
				p.best(data);
				tiempoTotal += System.currentTimeMillis() - tiempoActual;

				//System.out.println();
			}
			System.out.println(tiempoTotal/1000.0f/ejecuciones + " segundos");
		}*/

		/*{
			P04 p = new P04();
			p.best(4, "5 8 1 5 4 1 4 3 2 1 1 1 2 2 1 1");
			p.best(4, "5 8 1 5 4 1 4 3 6 1 1 1 2 2 1 1");
			p.best(2, "4 3 2 1 1 1");
			p.best(3, "1 5 4 1 4 3 6 1 1 1");
			p.best(10, "3 16 4 7 2 5 8 1 9 3 10 1 14 1 1 1 7 8 5 7 14 1 17 5 21 1 10 3 1 2 2 1");
		}*/

		/*{
			P06 p = new P06();
			String[] data = {
			"20 200",
			"4 6 4 7 8 2 6 3",
			"80 40 70 45 60 60 35 40",
			"250 770 640 440 880 960 240 330"
			};

			p.bestSolution(data);

		}*/

		/*{
			P07 p = new P07();
			String data = "55 70 66 65 88";
			System.out.println(p.best(data));
		}*/

		/*{
			System.out.println(Math.round(15.7f));

			P08 p = new P08();
			String data = "-.1 -.2 .1 -.2 .1 -.2";
			System.out.println(p.best(data));
		}*/

		/*{
			P09 p = new P09();
			String[] data = {"1000", "350 275 444 323 228"};
			System.out.println(p.best(data));
		}*/

		/*{
			P10 p = new P10();
			String data = "300 300 340 360";
			System.out.println(p.best(data));

			String data2 = "700 500 300 400 100 200";
			System.out.println(p.best(data2));
		}*/

		/*{
			P11 p = new P11();
			String data = "009005070\n081600000\n500001008\n050100000\n010807040\n000004010\n700400001\n000009320\n060200700";
			System.out.println(data);
			System.out.println(p.sol(data));
		}*/

		/*{
			P13 p = new P13();
			String[] data = {"100","3","5","3","5"};
			System.out.println(p.best(data));

			String[] data2 = {"871", "7", "2", "3", "3", "6", "5"};
			System.out.println(p.best(data2));
		}*/

		/*{
			P14 p = new P14();
			String data = "12 0 3";
			System.out.println(p.winner(data));
		}*/

		/*{
			P15 p = new P15();
			String data = "100 3 7 11";
			System.out.println(p.best(data));
			String data2 = "66 3 7 11";
			System.out.println(p.best(data2));
		}*/

		/*{
			P16 p = new P16();
			String data = "1 6 2 8 5 8 5 6";
			System.out.println(p.best(data));
		}*/

		/*{
			P17 p = new P17();
			String data = "3 10 12 14 16 15 18 20 22 20 13";
			System.out.println(p.best(data));
		}*/

		/*{
			P18 p = new P18();
			String data = "12 375 125 0 52 86 60 70 3 20 74 8 11 29 40 8";
			System.out.println(p.best(data));
			String data2 = "24 176 134 1 10 15 20 30 35 10 15 20 30 35 10 15 20 30 35 10 15 20 30 35 11 16 21 36";
			System.out.println(p.best(data2));
		}*/

		/*{
			P20 p = new P20();
			String data = "399 3 8439 1632 2044 1855 180 675 52 767 1440 195 29 64 5091 5104 5117 5130 5143 5156 5168 5181 5194 5207 5220 5233 6514 6548 6581 6615 6649 6683 0 0 0 0 0 0 5447 0 0 0 0 0 0 0 0 0 0 0";
			System.out.println(p.best(data));
			//399 3
			//8439 1632 2044 1855 180 675 52 767 1440 195 29 64
			//5091 5104 5117 5130 5143 5156 5168 5181 5194 5207 5220 5233
			//6514 6548 6581 6615 6649 6683 0 0 0 0 0 0
			//5447 0 0 0 0 0 0 0 0 0 0 0
		}*/

		/*{
			P21 p = new P21();
			String data = "100 3 12 7 6 11 4";
			int[] solucion = p.bestSolution(data);
			for (int i = 0; i < solucion.length; i++)
				System.out.print(solucion[i] + " ");
			System.out.println();
			System.out.println("--------------------------------------");

			String data2 = "1000 3 102 7 36 11 14 2 20 5 4 9 7 10 1 20 1 12 31";
			solucion = p.bestSolution(data2);
			for (int i = 0; i < solucion.length; i++)
				System.out.print(solucion[i] + " ");
			System.out.println();
			System.out.println("--------------------------------------");

			String data3 = "500 3 50 7 36 11 14 2 20 5 4 9 7 12 31";
			solucion = p.bestSolution(data3);
			for (int i = 0; i < solucion.length; i++)
				System.out.print(solucion[i] + " ");
			System.out.println();
			System.out.println("--------------------------------------");
		}*/

		/*{
			P84 p = new P84();
			String data = "100 3 12 7 6 11 4";
			System.out.println(p.best(data));
			System.out.println("--------------------------------------");

			String data2 = "1000 3 102 7 36 11 14 2 20 5 4 9 7 10 1 20 1 12 31";
			System.out.println(p.best(data2));
			System.out.println("--------------------------------------");

			String data3 = "500 3 50 7 36 11 14 2 20 5 4 9 7 12 31";
			System.out.println(p.best(data3));
			System.out.println("--------------------------------------");
		}*/

		/*{
			P30 p = new P30();
			String[] data = {"0 2",
			"0 1 20 30",
			"0 1 30 40",
			"1 0 35 45",
			"1 2 30 40",
			"1 2 35 45",
			"0 2 40 60"};
			System.out.println(p.best(data));
		}*/

		/*{
			//P81 p = new P81();
			P82 p = new P82();
			String[] data1 = {"8 2 10", "35 25 75","9 10 10"};
			p.best(data1);
			
			String[] data2 = {"7 16 19 17 28 6 22 14 12 12", "26 9 94 14 1 15 49 96 6 27", "2 9 10 16 36 53 69 85 86 86"};
			p.best(data2);

			String[] data3 = {
				"7 16 19 17 28 6 22 14 12 12 8 10 15 16 71 13 7",
				"26 9 94 14 1 15 49 96 6 27 20 21 23 4 8 9 30",
				"2 9 10 16 36 53 69 85 86 86 90 93 95 95 95 100 107"};
			p.best(data3);

			String[] data4 = {
				"7 16 19 17 28 6 22 14 12 12 8 10 15 16 71 13 7 10 28 79 40 3 50 9 8 9 11",
				"26 9 94 14 1 15 49 96 6 27 20 21 23 4 8 9 30 8 50 200 56 4 78 19 10 10 12",
				"2 9 10 16 36 53 69 85 86 86 90 93 95 95 95 100 107 107 107 108 109 110 115 120 127 137 147"};
			p.best(data4);

			String[] data5 = {
				"7 16 19 17 28 6 22 14 12 12 8 10 15 16 71 13 7 10 28 79 40 3 50 9 8 9 11 7 14 153",
				"26 9 94 14 1 15 49 96 6 27 20 21 23 4 8 9 30 8 50 200 56 4 78 19 10 10 12 7 14 1000",
				"2 9 10 16 36 53 69 85 86 86 90 93 95 95 95 100 107 107 107 108 109 110 115 120 127 137 147 150 151 170"};
			p.best(data5);

			String[] data6 = {
				"7 16 19 17 28 6 22 14 12 12 8 10 15 16 71 13 7 10 28 79 40 3 50 9 8 9 11 7 14 153 59 21 330 12 22 10 50",
				"26 9 94 14 1 15 49 96 6 27 20 21 23 4 8 9 30 8 50 200 56 4 78 19 10 10 12 7 14 1000 100 90 260 39 16 21 50",
				"2 9 10 16 36 53 69 85 86 86 90 93 95 95 95 100 107 107 107 108 109 110 115 120 127 137 147 150 151 170 180 181 181 182 195 207 237"};
			p.best(data6);

			String[] data7 = {
				"7 16 19 17 28 6 22 14 12 12 8 10 15 16 71 13 7 10 28 79 40 3 50 9 8 9 11 7 14 153 59 21 330 12 22 10 50 89 12 20 44 51 100 20 39 40 10 22 33 40 199 210 399 100 51",
				"26 9 94 14 1 15 49 96 6 27 20 21 23 4 8 9 30 8 50 200 56 4 78 19 10 10 12 7 14 1000 100 90 260 39 16 21 50 19 21 44 100 29 38 412 23 11 1 1 20 57 390 12 65 100 191",
				"2 9 10 16 36 53 69 85 86 86 90 93 95 95 95 100 107 107 107 108 109 110 115 120 127 137 147 150 151 170 180 181 181 182 195 207 237 247 251 300 350 355 357 358 360 362 365 370 400 400 400 400 400 400 410"};
			p.best(data7);
		}*/

		/*{
			P83 p = new P83();
			String data = "424 124 409 721 887 617 334 666 158 55";
			System.out.println(p.best(data));

			String data2 = "424 124 409 721 887 617 334 666 158 55 28 2300 12 145 123 1885";
			System.out.println(p.best(data2));
		}*/

		/*{
			P201 p = new P201();
			P202 p2 = new P202();
			String data = "12 375 125 0 52 86 60 70 3 20 74 8 11 29 40 8";
			int[] solucion = p.bestSolution(data);
			for (int i = 0; i < solucion.length; i++)
			{
				System.out.print(solucion[i] + " ");
			}
			System.out.println();


			String data2 = "24 176 134 1 10 15 20 30 35 10 15 20 30 35 10 15 20 30 35 10 15 20 30 35 11 16 21 36";
			System.out.println(p2.best(data2));
			solucion = p.bestSolution(data2);
			for (int i = 0; i < solucion.length; i++)
			{
				System.out.print(solucion[i] + " ");
			}
			System.out.println();
		}*/

		/*{
			P604 p = new P604();
			String data = "3 2 1 2 4 7 3";
			System.out.println(p.best(data));

			String data2 = "2 5 11 2 13 15 16 19 13 1 4 5";
			System.out.println(p.best(data2));

			String data3 = "10 4 172 40 345 354 3 2 3 11 2 3 22 78 45 3 23 877 776 98 11 123 44 12 12 42 1255 76";
			System.out.println(p.best(data3));
		}*/

		//System.exit(-1);

		/*tabla.put(10, 10, 10, 8);

		System.out.println(tabla.put(10, 10, 11, 27)); // 8
		System.out.println(tabla.put(10, 10, 13, 31)); // 27
		System.out.println(tabla.put(10, 10, 13, 79)); // 31
		System.out.println(tabla.put(10, 10, 14, 129)); // 79
		System.out.println(tabla.get(10, 12, 10)); // 79
		System.out.println(tabla.size());
		System.out.println("Elimina: " + tabla.remove(10, 10, 14));
		System.out.println(tabla.size());*/


		/*class Cuatro
		{
			Integer c1, c2, c3, c4;
			@Override
			public boolean equals(Object otro)
			{
				if (otro instanceof Cuatro)
				{
					return c1 == ((Cuatro) otro).c1 && c2 == ((Cuatro) otro).c2 && c3 == ((Cuatro) otro).c3 && c4 == ((Cuatro) otro).c4;
				}
				return false;
			}

			@Override
			public int hashCode()
			{
				return c1 + c2 + c3 + c4;
			}
		}

		// Se crean las tablas y se inicializan las variables.
		int iteraciones = 10000;
		long tiempo = 0;
		long tiempob = 0;
		long tiempoActual = 0;
		HashMap3<Integer, Integer, Integer, Integer> tabla = new HashMap3<Integer, Integer, Integer, Integer>();
		HashMap3b<Integer, Integer, Integer, Integer> tablab = new HashMap3b<Integer, Integer, Integer, Integer>((int) (iteraciones/0.75 + 1));
		HashSet<Cuatro> entradas = new HashSet<Cuatro>();

		// Se introducen valores y se contabilizan ls tiempos.
		for (int i = 0; i < iteraciones; i++)
		{
			Integer k1, k2, k3, v;
			k1 = new Integer((int) (Math.random()*100000)%100);
			k2 = new Integer((int) (Math.random()*100000)%100);
			k3 = new Integer((int) (Math.random()*100000)%100);
			v = new Integer((int) (Math.random()*100000));

			Cuatro entrada = new Cuatro();
			entrada.c1 = k1;
			entrada.c2 = k2;
			entrada.c3 = k3;
			entrada.c4 = v;
			entradas.add(entrada);

			tiempoActual = System.currentTimeMillis();
			
			tabla.put(k1, k2, k3, v);

			tiempo += System.currentTimeMillis() - tiempoActual;
			tiempoActual = System.currentTimeMillis();

			tablab.put(k1, k2, k3, v);

			tiempob += System.currentTimeMillis() - tiempoActual;
		}

		System.out.println("Elementos: " + tabla.isEmpty());
		System.out.println("Elementos: " + tablab.isEmpty());
		System.out.println("Tiempo: " + tiempo); tiempo = 0;
		System.out.println("Tiempob: " + tiempob); tiempob = 0;
		System.out.println("...................");

		// Se accede a los valores y se contabilizan los tiempos.
		Iterator iterador = entradas.iterator();
		while (iterador.hasNext())
		{
			Cuatro entrada = (Cuatro) iterador.next();

			tiempoActual = System.currentTimeMillis();

			tabla.get(entrada.c1, entrada.c2, entrada.c3);

			tiempo += System.currentTimeMillis() - tiempoActual;
			tiempoActual = System.currentTimeMillis();

			tablab.get(entrada.c1, entrada.c2, entrada.c3);

			tiempob += System.currentTimeMillis() - tiempoActual;
		}

		System.out.println("Elementos: " + tabla.isEmpty());
		System.out.println("Elementos: " + tablab.isEmpty());
		System.out.println("Tiempo: " + tiempo); tiempo = 0;
		System.out.println("Tiempob: " + tiempob); tiempob = 0;
		System.out.println("...................");

		// Se eliminan los valores 1 a 1.
		iterador = entradas.iterator();
		while (iterador.hasNext())
		{
			Cuatro entrada = (Cuatro) iterador.next();

			tiempoActual = System.currentTimeMillis();

			tabla.remove(entrada.c1, entrada.c2, entrada.c3);

			tiempo += System.currentTimeMillis() - tiempoActual;
			tiempoActual = System.currentTimeMillis();

			tablab.remove(entrada.c1, entrada.c2, entrada.c3);

			tiempob += System.currentTimeMillis() - tiempoActual;
		}

		System.out.println("Elementos: " + tabla.isEmpty());
		System.out.println("Elementos: " + tablab.isEmpty());
		System.out.println("Tiempo: " + tiempo); tiempo = 0;
		System.out.println("Tiempob: " + tiempob); tiempob = 0;
		System.out.println("...................");
		 *
		 * */
	}
}

/*

class HashMap2<K1, K2, V>
{
    private HashMap<Par, V> tabla;

    public HashMap2()
    {
        tabla = new HashMap<Par, V>();
    }

    public HashMap2(int initialCapacity)
    {
        tabla = new HashMap<Par, V>(initialCapacity);
    }

    public HashMap2(int initialCapacity, float loadFactor)
    {
        tabla = new HashMap<Par, V>(initialCapacity, loadFactor);
    }

    public V get(K1 key1, K2 key2)
    {
        return tabla.get(new Par(key1, key2));
    }

    public V remove(K1 key1, K2 key2)
    {
        return tabla.remove(new Par(key1, key2));
    }

    public V put(K1 key1, K2 key2, V value)
    {
        return tabla.put(new Par(key1, key2), value);
    }

    public boolean containsKey(K1 key1, K2 key2)
    {
        return tabla.containsKey(new Par(key1, key2));
    }

    public boolean containsValue(V value)
    {
        return tabla.containsValue(value);
    }

    public boolean isEmpty()
    {
        return tabla.isEmpty();
    }

    public int size()
    {
        return tabla.size();
    }

    public void clear()
    {
        tabla.clear();
    }

    @Override
    public HashMap2<K1, K2, V> clone()
    {
        HashMap2<K1, K2, V> copia = new HashMap2<K1, K2, V>();
        copia.tabla = (HashMap<Par, V>) tabla.clone();
        return copia;
    }

    public Collection<V> values()
    {
        return tabla.values();
    }

    private static class Par
    {
        private Object objeto1;
        private Object objeto2;

        public Par(Object objeto1, Object objeto2)
        {
            this.objeto1 = objeto1;
            this.objeto2 = objeto2;
        }

        @Override
        public boolean equals(Object otroPar)
        {
            if (otroPar instanceof Par)
            {
                Par par = (Par) otroPar;
                return objeto1.equals(par.objeto1) && objeto2.equals(par.objeto2);
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            int resultado = 17 * 37 + objeto1.hashCode();
            resultado = 37 * resultado + objeto2.hashCode();
            return resultado;
        }

        @Override
        public String toString()
        {
            return "<" + objeto1.toString() + ", " + objeto2.toString() + ">";
        }
    }
}

class HashMap3b<K1, K2, K3, V>
{
    private HashMap<Par, V> tabla;

    public HashMap3b()
    {
        tabla = new HashMap<Par, V>();
    }

    public HashMap3b(int initialCapacity)
    {
        tabla = new HashMap<Par, V>(initialCapacity);
    }

    public HashMap3b(int initialCapacity, float loadFactor)
    {
        tabla = new HashMap<Par, V>(initialCapacity, loadFactor);
    }

    public V get(K1 key1, K2 key2, K3 key3)
    {
        return tabla.get(new Par(key1, key2, key3));
    }

    public V remove(K1 key1, K2 key2, K3 key3)
    {
        return tabla.remove(new Par(key1, key2, key3));
    }

    public V put(K1 key1, K2 key2, K3 key3, V value)
    {
        return tabla.put(new Par(key1, key2, key3), value);
    }

    public boolean containsKey(K1 key1, K2 key2, K3 key3)
    {
        return tabla.containsKey(new Par(key1, key2, key3));
    }

    public boolean containsValue(V value)
    {
        return tabla.containsValue(value);
    }

    public boolean isEmpty()
    {
        return tabla.isEmpty();
    }

    public int size()
    {
        return tabla.size();
    }

    public void clear()
    {
        tabla.clear();
    }

    @Override
    public HashMap3b<K1, K2, K3, V> clone()
    {
        HashMap3b<K1, K2, K3, V> copia = new HashMap3b<K1, K2, K3, V>();
        copia.tabla = (HashMap<Par, V>) tabla.clone();
        return copia;
    }

    public Collection<V> values()
    {
        return tabla.values();
    }

    private static class Par
    {
        private Object objeto1;
        private Object objeto2;
		private Object objeto3;

        public Par(Object objeto1, Object objeto2, Object objeto3)
        {
            this.objeto1 = objeto1;
            this.objeto2 = objeto2;
			this.objeto3 = objeto3;
        }

        @Override
        public boolean equals(Object otroPar)
        {
            if (otroPar instanceof Par)
            {
                Par par = (Par) otroPar;
                return objeto1.equals(par.objeto1) && objeto2.equals(par.objeto2) && objeto3.equals(par.objeto3);
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            int resultado = 17 * 37 + objeto1.hashCode();
            resultado = 37 * resultado + objeto2.hashCode();
			resultado = 37 * resultado + objeto3.hashCode();
            return resultado;
        }

        @Override
        public String toString()
        {
            return "<" + objeto1.toString() + ", " + objeto2.toString() + ", " + objeto3.toString() + ">";
        }
    }
}

class HashMap3<K1, K2, K3, V>
{
	private HashMap<K1, HashMap<K2, HashMap<K3, V>>> table;
	private int size;
	
	public HashMap3()
	{
		table = new HashMap<K1, HashMap<K2, HashMap<K3, V>>>();
		size = 0;
	}

	public V put(K1 key1, K2 key2, K3 key3, V value)
	{
		HashMap<K1, HashMap<K2, HashMap<K3, V>>> table1 = table;
		HashMap<K2, HashMap<K3, V>> table2 = null;
		HashMap<K3, V> table3 = null;

		if (!table1.containsKey(key1))
		{
			table2 = new HashMap<K2, HashMap<K3, V>>();
			table1.put(key1, table2);
		}
		else
		{
			table2 = table1.get(key1);
		}

		if (!table2.containsKey(key2))
		{
			table3 = new HashMap<K3, V>();
			table2.put(key2, table3);
		}
		else
		{
			table3 = table2.get(key2);
		}

		V previous = table3.put(key3, value);
		if (previous == null)
		{
			size++;
		}
		return previous;
	}

	public V get(K1 key1, K2 key2, K3 key3)
	{
		HashMap<K1, HashMap<K2, HashMap<K3, V>>> table1 = table;
		HashMap<K2, HashMap<K3, V>> table2 = null;
		HashMap<K3, V> table3 = null;

		if (table1.containsKey(key1))
		{
			table2 = table1.get(key1);
			if (table2.containsKey(key2))
			{
				table3 = table2.get(key2);
				return table3.get(key3);
			}
		}

		return null;
	}

	public int size()
	{
		return size;
	}

	public boolean isEmpty()
	{
		return size == 0;
	}

	public V remove(K1 key1, K2 key2, K3 key3)
	{
		HashMap<K1, HashMap<K2, HashMap<K3, V>>> table1 = table;
		HashMap<K2, HashMap<K3, V>> table2 = null;
		HashMap<K3, V> table3 = null;

		if (table1.containsKey(key1))
		{
			table2 = table1.get(key1);
			if (table2.containsKey(key2))
			{
				table3 = table2.get(key2);
				V value = table3.remove(key3);
				if (value != null)
				{
					size--;
				}
				return value;
			}
		}

		return null;
	}
}
 *
 * */