package fuzzy;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Line;
import net.phys2d.raw.BasicJoint;
import net.phys2d.raw.Joint;
import java.awt.event.KeyEvent;

/**
 * Terreno
 */
public class PhysApp extends AbstractDemo
{
	private FuzzyPendulumController controller;

	private Body suelo;
	private Body pared1, pared2;
	private Body plataforma;
	private Body pendulo;
	private float tamanoPendulo = 150f;
	private Joint enlace;

	private Body raton;

	private boolean izquierda;
	private boolean derecha;

	private boolean sistemaexperto;

	/**
	 * Create the demo
	 */
	public PhysApp()
	{
		super("Práctica FIA 09-10 - Péndulo invertido con sistema experto difuso", 1000, 550);

		sistemaexperto = true;
	}

	public void setController(FuzzyPendulumController c)
	{
		controller = c;
	}

	protected void init(World world)
	{
		pared1 = new StaticBody("pared1", new Box(10, 150));
		pared1.setPosition(10, 305);
		world.add(pared1);
		pared2 = new StaticBody("pared2", new Box(10, 150));
		pared2.setPosition(990, 305);
		world.add(pared2);

		suelo = new StaticBody("suelo", new Line(0, 380, 1000, 380));
		suelo.setFriction(0.001f);
		world.add(suelo);

		plataforma = new Body("plataforma", new Box(30.0f, 10.0f), 20f);
		plataforma.setPosition(500.0f, 370.0f);
		world.add(plataforma);

		pendulo = new Body("pendulo", new Box(1f, tamanoPendulo), 0.000000001f);
		pendulo.setPosition(500.0f, 370.0f - tamanoPendulo/2);
		world.add(pendulo);

		raton = new StaticBody("puntero", new Circle(30f));
		raton.setPosition(-10f, -10f);
		world.add(raton);

		raton.addExcludedBody(plataforma);
		pendulo.addExcludedBody(plataforma);
		pendulo.addExcludedBody(suelo);

		enlace = new BasicJoint(plataforma, pendulo, new Vector2f(500.0f, 370.0f));
		world.add(enlace);
	}

	@Override
	public void keyHit(KeyEvent e)
	{
		super.keyHit(e);

		char c = e.getKeyChar();
		
		if (e.getKeyCode() == 37)
		{
			izquierda = true;
		}
		else
		{
			if (e.getKeyCode() == 39)
			{
				derecha = true;
			}
			else
			{
				if (e.getKeyCode() == 109)
				{
					tamanoPendulo--;
					needsReset = true;
				}
				else
				{
					if (e.getKeyCode() == 107)
					{
						tamanoPendulo++;
						needsReset = true;
					}
					else
					{
						if (c == 't')
						{
							pendulo.setPosition(pendulo.getPosition().getX() - plataforma.getPosition().getX() + 500, pendulo.getPosition().getY());
							plataforma.setPosition(500, plataforma.getPosition().getY());
						}
						else
						{
							if (c == 's')
							{
								sistemaexperto = !sistemaexperto;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void keyUnhit(KeyEvent e)
	{
		if (e.getKeyCode() == 37)
		{
			izquierda = false;
		}
		else
		{
			if (e.getKeyCode() == 39)
			{
				derecha = false;
			}
		}
	}

	@Override
	public void mouseHit(int x, int y)
	{
		raton.setPosition(x, y);
	}

	@Override
	public void mouseHit(int button, boolean pressed)
	{
		if (!pressed)
			raton.setPosition(-10f, -10f);
	}

	@Override
	public void plataformaHit(int x)
	{
		plataforma.setPosition(x, plataforma.getPosition().getY());
	}

	private long tiempoTranscurrido = 0;
	private long tiempoActual = System.currentTimeMillis();

	@Override
	protected void update()
	{
		super.update();

		// Actualización de las variables del sistema.
		ROVector2f posicionPlataforma = plataforma.getPosition();
		ROVector2f posicionPendulo = pendulo.getPosition();
		velocidad = pendulo.getAngularVelocity();
		velocidadlineal = pendulo.getVelocity().getX();
		angulo = (float) Math.atan((posicionPendulo.getX() - posicionPlataforma.getX()) / (posicionPendulo.getY() - posicionPlataforma.getY()));
		//angulo = angulo*180 / (float) Math.PI + ((posicionPlataforma.getY() >= posicionPendulo.getY()) ? 90 : 270); // de 0 a 360º
		angulo = angulo*180 / (float) Math.PI;
		if (posicionPlataforma.getY() < posicionPendulo.getY())
		{
			if (posicionPlataforma.getX() > posicionPendulo.getX())
			{
				angulo += 180;
			}
			else
			{
				angulo -= 180;
			}
		}
		angulo *= -1;
		posicion = posicionPlataforma.getX() - 500;
		vplataforma = plataforma.getVelocity().getX();

		// Si el péndulo está tocando una pared, lo empujamos hacia el centro.
		if (((int) (angulo * 10)) == 57 && ((int) posicion) == 470)
			pendulo.adjustAngularVelocity(-0.3f);
		if (((int) (angulo * 10)) == -57 && ((int) posicion) == -470)
			pendulo.adjustAngularVelocity(0.3f);

		// Control de la plataforma con el sistema experto difuso.
		tiempoTranscurrido += System.currentTimeMillis() - tiempoActual;
		tiempoActual = System.currentTimeMillis();
		if (sistemaexperto && tiempoTranscurrido >= 0)
		{
			float aceleracion = controller.step(velocidad, angulo, posicion, velocidadlineal);
			//System.out.println(aceleracion);
			plataforma.addForce(new Vector2f(aceleracion, 0));
			//System.out.println("Sistema experto!!");
			tiempoTranscurrido = 0;
		}

		// Control de la plataforma con el teclado.
		if (izquierda)
			plataforma.addForce(new Vector2f(-1000, 0));
		if (derecha)
			plataforma.addForce(new Vector2f(1000, 0));
	}
}
