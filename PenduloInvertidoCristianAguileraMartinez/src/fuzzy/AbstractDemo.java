package fuzzy;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.RenderingHints;

import net.phys2d.math.MathUtil;
import net.phys2d.math.Matrix2f;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.AngleJoint;
import net.phys2d.raw.Arbiter;
import net.phys2d.raw.ArbiterList;
import net.phys2d.raw.BasicJoint;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.Contact;
import net.phys2d.raw.DistanceJoint;
import net.phys2d.raw.FixedJoint;
import net.phys2d.raw.Joint;
import net.phys2d.raw.JointList;
import net.phys2d.raw.SlideJoint;
import net.phys2d.raw.SpringJoint;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Line;
import net.phys2d.raw.shapes.Polygon;
import net.phys2d.raw.strategies.QuadSpaceStrategy;

/**
 * A common demo box super class.
 *
 * @author Kevin Glass
 */
public abstract class AbstractDemo
{

	/** The frame displaying the demo */
	protected Frame frame;
	/** The title of the current demo */
	protected String title;
	/** The world containing the physics model */
	protected World world = new World(new Vector2f(0.0f, 10.0f), 10, new QuadSpaceStrategy(20, 5));
	/** True if the simulation is running */
	private boolean running = true;
	/** The rendering strategy */
	private BufferStrategy strategy;
	/** True if we should reset the demo on the next loop */
	protected boolean needsReset;
	/** True if we should render normals */
	private boolean normals = true;
	/** True if we should render contact points */
	private boolean contacts = true;
	int width;
	int height;

	float velocidad;
	float velocidadlineal;
	float angulo;
	float posicion;
	float vplataforma;

	boolean antialiasing = true;

	/**
	 * Create a new demo
	 *
	 * @param title The title of the demo
	 */
	public AbstractDemo(String title, int width, int height)
	{
		this.title = title;
		this.width = width;
		this.height = height;
	}

	/**
	 * Retrieve the title of the demo
	 *
	 * @return The title of the demo
	 */
	public String getTitle()
	{
		return title;
	}

	private void drawGoal(Graphics2D g, float x, float y)
	{

		g.setColor(Color.red);
		float r = 10;

		g.fillOval((int) (x - r), (int) (y - r), (int) (r * 2), (int) (r * 2));
	}

	/**
	 * Notification that a key was pressed
	 *
	 * @param c The character of key hit
	 */
	protected void keyHit(KeyEvent e)
	{
		char c = e.getKeyChar();
		if (c == 'r')
		{
			needsReset = true;
		}
		if (c == 'c')
		{
			normals = !normals;
			contacts = !contacts;
		}
		if (c == 'a')
		{
			antialiasing = !antialiasing;
		}
	}

	protected void keyUnhit(KeyEvent e)
	{
		
	}

	protected void mouseHit(int x, int y)
	{
		
	}

	protected void mouseHit(int button, boolean pressed)
	{

	}

	protected void plataformaHit(int x)
	{
		
	}

	/**
	 * Initialise the GUI
	 */
	private void initGUI()
	{
		frame = new Frame(title);
		frame.setResizable(false);
		frame.setIgnoreRepaint(true);
		frame.setSize(width, height);

		int x = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - width) / 2;
		int y = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - height) / 2;

		frame.setLocation(x, y);

		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				running = false;
				System.exit(0);
			}
		});

		frame.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent e)
			{
				mouseHit(e.getX(), e.getY());
			}
		});

		frame.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				mouseHit(e.getX(), e.getY());
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				mouseHit(e.getButton(), false);
			}
		});

		frame.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				keyUnhit(e);
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				keyHit(e);
				if (e.getKeyCode() == 27)
				{
					System.exit(0);
				}
			}
		});

		frame.setVisible(true);
		frame.createBufferStrategy(2);

		strategy = frame.getBufferStrategy();
	}

	/**
	 * Start the simulation running
	 */
	public void start()
	{
		initGUI();
		initDemo();

		float target = 1000 / 60.0f;
		float frameAverage = target;
		long lastFrame = System.currentTimeMillis();
		float yield = 10000f;
		float damping = 0.1f;

		long renderTime = 0;
		long logicTime = 0;

		while (running)
		{
			// adaptive timing loop from Master Onyx
			long timeNow = System.currentTimeMillis();
			frameAverage = (frameAverage * 10 + (timeNow - lastFrame)) / 11;
			lastFrame = timeNow;

			yield += yield * ((target / frameAverage) - 1) * damping + 0.05f;

			for (int i = 0; i < yield; i++)
			{
				Thread.yield();
			}

			// render
			long beforeRender = System.currentTimeMillis();
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, width, height);
			if (antialiasing)
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);


			draw(g);
			renderGUI(g);
			/*g.setColor(Color.black);
			g.drawString("FAv: "+frameAverage,10,50);
			g.drawString("FPS: "+(int) (1000 / frameAverage),10,70);
			g.drawString("Yield: "+yield,10,90);
			g.drawString("Arbiters: "+world.getArbiters().size(),10,110);
			g.drawString("Bodies: "+world.getBodies().size(),10,130);
			g.drawString("R: "+renderTime,10,150);
			g.drawString("L: "+logicTime,10,170);
			g.drawString("Energy: "+world.getTotalEnergy(),10,190);*/

			g.drawString("Velocidad angular del péndulo: " + velocidad, 15, 45);
			g.drawString("Velocidad lineal del péndulo: " + velocidadlineal, 15, 65);
			g.drawString("Ángulo de desviación: " + angulo, 15, 85);
			g.drawString("Posición de la plataforma: " + posicion, 15, 105);
			//g.drawString("Velocidad de la plataforma: " + vplataforma, 15, 125);
			g.dispose();
			strategy.show();
			renderTime = System.currentTimeMillis() - beforeRender;

			// update data model
			long beforeLogic = System.currentTimeMillis();
			for (int i = 0; i < 5; i++)
			{
				world.step();
			}
			logicTime = System.currentTimeMillis() - beforeLogic;

			if (needsReset)
			{
				world.clear();
				//initDemo();
				init(world);
				needsReset = false;
				frameAverage = target;
				yield = 10000f;
			}

			update();
		}
	}

	/**
	 * Update the demo - just in case we want to add anything over
	 * the top
	 */
	protected void update()
	{
	}

	/**
	 * Demo customisable GUI render
	 *
	 * @param g The graphics context to use for rendering here
	 */
	protected void renderGUI(Graphics2D g)
	{
		g.setColor(Color.black);
		g.drawString("Con el botón principal del ratón puede modificarse la posición del péndulo", 15, 440);
		g.drawString("Con los cursores (←→) puede desplazarse la plataforma manualmente", 15, 460);
		g.drawString("R - Reiniciar la simulación", 15, 490);
		//g.drawString("T - Trasladar la plataforma y el péndulo al centro de la ventana (mantiene el ángulo y la velocidad angular)", 15, 490);
		g.drawString("S - Activar o desactivar el sistema experto que equilibra el péndulo", 15, 510);
		g.drawString("A - Activar o desactivar antialiasing", 15, 530);
		//g.drawString("C - Mostrar/Ocultar vectores normales y puntos de contacto", 15, 390);
		//g.drawString("N/M - Desplazamiento manual de la plataforma", 15, 430);

		g.drawString("[+] - Incrementar longitud del péndulo", 650, 510);
		g.drawString("[-] - Decrementar longitud del péndulo", 650, 530);
	}

	/**
	 * Draw a specific contact point determined from the simulation
	 *
	 * @param g The graphics context on which to draw
	 * @param contact The contact to draw
	 */
	protected void drawContact(Graphics2D g, Contact contact)
	{
		int x = (int) contact.getPosition().getX();
		int y = (int) contact.getPosition().getY();
		if (contacts)
		{
			g.setColor(Color.blue);
			g.fillOval(x - 3, y - 3, 6, 6);
		}

		if (normals)
		{
			int dx = (int) (contact.getNormal().getX() * 10);
			int dy = (int) (contact.getNormal().getY() * 10);
			g.setColor(Color.darkGray);
			g.drawLine(x, y, x + dx, y + dy);
		}
	}

	/**
	 * Draw a body
	 *
	 * @param g The graphics contact on which to draw
	 * @param body The body to be drawn
	 */
	protected void drawBody(Graphics2D g, Body body)
	{
		if (body.getShape() instanceof Box)
		{
			drawBoxBody(g, body, (Box) body.getShape());
		}
		if (body.getShape() instanceof Circle)
		{
			drawCircleBody(g, body, (Circle) body.getShape());
		}
		if (body.getShape() instanceof Line)
		{
			drawLineBody(g, body, (Line) body.getShape());
		}
		if (body.getShape() instanceof Polygon)
		{
			drawPolygonBody(g, body, (Polygon) body.getShape());
		}
	}

	/**
	 * Draw a polygon into the demo
	 *
	 * @param g The graphics to draw the poly onto
	 * @param body The body describing the poly's position
	 * @param poly The poly to be drawn
	 */
	protected void drawPolygonBody(Graphics2D g, Body body, Polygon poly)
	{
		g.setColor(Color.black);

		ROVector2f[] verts = poly.getVertices(body.getPosition(), body.getRotation());
		for (int i = 0, j = verts.length - 1; i < verts.length; j = i, i++)
		{
			g.drawLine(
				(int) (0.5f + verts[i].getX()),
				(int) (0.5f + verts[i].getY()),
				(int) (0.5f + verts[j].getX()),
				(int) (0.5f + verts[j].getY()));
		}
	}

	/**
	 * Draw a line into the demo
	 *
	 * @param g The graphics to draw the line onto
	 * @param body The body describing the line's position
	 * @param line The line to be drawn
	 */
	protected void drawLineBody(Graphics2D g, Body body, Line line)
	{
		g.setColor(Color.black);
//
//		float x = body.getPosition().getX();
//		float y = body.getPosition().getY();
//		float dx = line.getDX();
//		float dy = line.getDY();
//
//		g.drawLine((int) x,(int) y,(int) (x+dx),(int) (y+dy));
		Vector2f[] verts = line.getVertices(body.getPosition(), body.getRotation());
		g.drawLine(
			(int) verts[0].getX(),
			(int) verts[0].getY(),
			(int) verts[1].getX(),
			(int) verts[1].getY());
	}

	/**
	 * Draw a circle in the world
	 *
	 * @param g The graphics contact on which to draw
	 * @param body The body to be drawn
	 * @param circle The shape to be drawn
	 */
	protected void drawCircleBody(Graphics2D g, Body body, Circle circle)
	{
		g.setColor(Color.black);
		float x = body.getPosition().getX();
		float y = body.getPosition().getY();
		float r = circle.getRadius();
		float rot = body.getRotation();
		float xo = (float) (Math.cos(rot) * r);
		float yo = (float) (Math.sin(rot) * r);

				
		g.setColor(Color.ORANGE);
		g.fillOval((int) (x - r), (int) (y - r), (int) (r * 2), (int) (r * 2));
		g.setColor(Color.black);
		g.drawOval((int) (x - r), (int) (y - r), (int) (r * 2), (int) (r * 2));
		g.drawLine((int) x, (int) y, (int) (x + xo), (int) (y + yo));
	}

	/**
	 * Draw a box in the world
	 *
	 * @param g The graphics contact on which to draw
	 * @param body The body to be drawn
	 * @param box The shape to be drawn
	 */
	protected void drawBoxBody(Graphics2D g, Body body, Box box)
	{
		Vector2f[] pts = box.getPoints(body.getPosition(), body.getRotation());

		Vector2f v1 = pts[0];
		Vector2f v2 = pts[1];
		Vector2f v3 = pts[2];
		Vector2f v4 = pts[3];

		g.setColor(Color.black);
		g.drawLine((int) v1.x, (int) v1.y, (int) v2.x, (int) v2.y);
		g.drawLine((int) v2.x, (int) v2.y, (int) v3.x, (int) v3.y);
		g.drawLine((int) v3.x, (int) v3.y, (int) v4.x, (int) v4.y);
		g.drawLine((int) v4.x, (int) v4.y, (int) v1.x, (int) v1.y);
	}

	/**
	 * Draw a joint
	 *
	 * @param g The graphics contact on which to draw
	 * @param j The joint to be drawn
	 */
	public void drawJoint(Graphics2D g, Joint j)
	{
		if (j instanceof FixedJoint)
		{
			FixedJoint joint = (FixedJoint) j;

			g.setColor(Color.red);
			float x1 = joint.getBody1().getPosition().getX();
			float x2 = joint.getBody2().getPosition().getX();
			float y1 = joint.getBody1().getPosition().getY();
			float y2 = joint.getBody2().getPosition().getY();

			g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
		}
		if (j instanceof SlideJoint)
		{
			SlideJoint joint = (SlideJoint) j;

			Body b1 = joint.getBody1();
			Body b2 = joint.getBody2();

			Matrix2f R1 = new Matrix2f(b1.getRotation());
			Matrix2f R2 = new Matrix2f(b2.getRotation());

			ROVector2f x1 = b1.getPosition();
			Vector2f p1 = MathUtil.mul(R1, joint.getAnchor1());
			p1.add(x1);

			ROVector2f x2 = b2.getPosition();
			Vector2f p2 = MathUtil.mul(R2, joint.getAnchor2());
			p2.add(x2);

			Vector2f im = new Vector2f(p2);
			im.sub(p1);
			im.normalise();



			g.setColor(Color.red);
			g.drawLine((int) p1.x, (int) p1.y, (int) (p1.x + im.x * joint.getMinDistance()), (int) (p1.y + im.y * joint.getMinDistance()));
			g.setColor(Color.blue);
			g.drawLine((int) (p1.x + im.x * joint.getMinDistance()), (int) (p1.y + im.y * joint.getMinDistance()), (int) (p1.x + im.x * joint.getMaxDistance()), (int) (p1.y + im.y * joint.getMaxDistance()));
		}
		if (j instanceof AngleJoint)
		{
			AngleJoint angleJoint = (AngleJoint) j;
			Body b1 = angleJoint.getBody1();
			Body b2 = angleJoint.getBody2();
			float RA = j.getBody1().getRotation() + angleJoint.getRotateA();
			float RB = j.getBody1().getRotation() + angleJoint.getRotateB();

			Vector2f VA = new Vector2f((float) Math.cos(RA), (float) Math.sin(RA));
			Vector2f VB = new Vector2f((float) Math.cos(RB), (float) Math.sin(RB));

			Matrix2f R1 = new Matrix2f(b1.getRotation());
			Matrix2f R2 = new Matrix2f(b2.getRotation());

			ROVector2f x1 = b1.getPosition();
			Vector2f p1 = MathUtil.mul(R1, angleJoint.getAnchor1());
			p1.add(x1);

			ROVector2f x2 = b2.getPosition();
			Vector2f p2 = MathUtil.mul(R2, angleJoint.getAnchor2());
			p2.add(x2);

			g.setColor(Color.red);
			g.drawLine((int) p1.x, (int) p1.y, (int) (p1.x + VA.x * 20), (int) (p1.y + VA.y * 20));
			g.drawLine((int) p1.x, (int) p1.y, (int) (p1.x + VB.x * 20), (int) (p1.y + VB.y * 20));
		}
		if (j instanceof BasicJoint)
		{
			BasicJoint joint = (BasicJoint) j;

			Body b1 = joint.getBody1();
			Body b2 = joint.getBody2();

			Matrix2f R1 = new Matrix2f(b1.getRotation());
			Matrix2f R2 = new Matrix2f(b2.getRotation());

			ROVector2f x1 = b1.getPosition();
			Vector2f p1 = MathUtil.mul(R1, joint.getLocalAnchor1());
			p1.add(x1);

			ROVector2f x2 = b2.getPosition();
			Vector2f p2 = MathUtil.mul(R2, joint.getLocalAnchor2());
			p2.add(x2);

			g.setColor(Color.black);
			//g.drawLine((int) x1.getX(), (int) x1.getY(), (int) p1.x, (int) p1.y);
			g.drawLine((int) p1.x, (int) p1.y, (int) x2.getX(), (int) x2.getY());
			//g.drawLine((int) x2.getX(), (int) x2.getY(), (int) p2.x, (int) p2.y);
			//g.drawLine((int) p2.x, (int) p2.y, (int) x1.getX(), (int) x1.getY());
		}
		if (j instanceof DistanceJoint)
		{
			DistanceJoint joint = (DistanceJoint) j;

			Body b1 = joint.getBody1();
			Body b2 = joint.getBody2();

			Matrix2f R1 = new Matrix2f(b1.getRotation());
			Matrix2f R2 = new Matrix2f(b2.getRotation());

			ROVector2f x1 = b1.getPosition();
			Vector2f p1 = MathUtil.mul(R1, joint.getAnchor1());
			p1.add(x1);

			ROVector2f x2 = b2.getPosition();
			Vector2f p2 = MathUtil.mul(R2, joint.getAnchor2());
			p2.add(x2);

			g.setColor(Color.red);
			g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.x, (int) p2.y);
		}
		if (j instanceof SpringJoint)
		{
			SpringJoint joint = (SpringJoint) j;

			Body b1 = joint.getBody1();
			Body b2 = joint.getBody2();

			Matrix2f R1 = new Matrix2f(b1.getRotation());
			Matrix2f R2 = new Matrix2f(b2.getRotation());

			ROVector2f x1 = b1.getPosition();
			Vector2f p1 = MathUtil.mul(R1, joint.getLocalAnchor1());
			p1.add(x1);

			ROVector2f x2 = b2.getPosition();
			Vector2f p2 = MathUtil.mul(R2, joint.getLocalAnchor2());
			p2.add(x2);

			g.setColor(Color.red);
			g.drawLine((int) x1.getX(), (int) x1.getY(), (int) p1.x, (int) p1.y);
			g.drawLine((int) p1.x, (int) p1.y, (int) p2.getX(), (int) p2.getY());
			g.drawLine((int) p2.getX(), (int) p2.getY(), (int) x2.getX(), (int) x2.getY());
		}
	}

	/**
	 * Draw the whole simulation
	 *
	 * @param g The graphics context on which to draw
	 */
	protected void draw(Graphics2D g)
	{
		//drawGoal(g, goalX, goalY);

		BodyList bodies = world.getBodies();

		for (int i = 0; i < bodies.size(); i++)
		{
			Body body = bodies.get(i);

			drawBody(g, body);
		}

		/*JointList joints = world.getJoints();

		for (int i = 0; i < joints.size(); i++)
		{
			Joint joint = joints.get(i);

			drawJoint(g, joint);
		}*/

		ArbiterList arbs = world.getArbiters();

		for (int i = 0; i < arbs.size(); i++)
		{
			Arbiter arb = arbs.get(i);

			Contact[] contacts2 = arb.getContacts();
			int numContacts = arb.getNumContacts();

			for (int j = 0; j < numContacts; j++)
			{
				drawContact(g, contacts2[j]);
			}
		}
	}

	/**
	 * Initialise the demo - clear the world
	 */
	public final void initDemo()
	{
		world.clear();
		world.setGravity(0, 10);

		System.out.println("Initialising:" + getTitle());
		init(world);
	}

	/**
	 * Should be implemented by the demo, add the bodies/joints
	 * to the world.
	 *
	 * @param world The world in which the simulation is going to run
	 */
	protected abstract void init(World world);
}
