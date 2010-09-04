package fuzzy0910;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Line;


/**
 * Terreno
 */
public class PhysApp extends AbstractDemo {
	/** The box falling into the simulation */
	private Body car;
        private BrakeController controller;

	/**
	 * Create the demo
	 */
	public PhysApp() {
            super("Práctica Fuzzy FIA 09-10",1000,500);
	}

        public void setController(BrakeController c){
            controller = c;
        }



	protected void init(World world) {

                //Distancia minima en la que se encuentra la meta
                float minDist=100;


		Body land = new StaticBody("Line1", new Line(130,30));
		land.setPosition(-30,200);
		world.add(land);
		Body land2 = new StaticBody("Line2", new Line(50,50));
		land2.setPosition(100,230);
		world.add(land2);
		Body land3 = new StaticBody("Line3", new Line(100,20));
		land3.setPosition(150,280);
		world.add(land3);
		Body land4 = new StaticBody("Line4", new Line(100,80));
		land4.setPosition(250,300);
		world.add(land4);
		Body land5 = new StaticBody("Line5", new Line(550,0));
		land5.setPosition(350,380);
		world.add(land5);

                //Fricción del suelo
                mu =(float) (Math.random() * 0.03);

		car = new Body("Faller", new Box(20,10), 1);
                car.setFriction(mu);
		car.setPosition(50,50);
		car.setRotation(-0.5f);
		world.add(car);

                goalX =  350 + iteraciones;//minDist + (int) (Math.random() * (550-minDist));
                goalY = 380;
                
                ROVector2f v = car.getPosition();
                dist = (float) Math.sqrt((goalX-v.getX())*(goalX -v.getX()));

		
	}

        public void keyHit(char c) {
		super.keyHit(c);
                ROVector2f v;

                v=car.getPosition();

		if (c == 'a') {

                    //El coche esta en movimiento y no va hacia atras
                    if(car.getEnergy()>=1 && car.getVelocity().getX()>=0){
                        //Solo permito aplicar fuerzas si se encuentra en la parte recta
                        if(v.getY()>=375f){
                            car.addForce(new Vector2f(-200,0));
                        } else {
                             System.out.println("No se puede frenar fuera de la recta");
                        }
                    }

		}


	}

	public int maxIteraciones = 500;
	public int iteraciones = 100;
	public float velocidadMaxima = 34.3f;

	protected void update() {
		super.update();
                
                //Calculo la distancia a la meta
                ROVector2f v = car.getPosition();
                dist = (float) Math.sqrt((goalX-v.getX())*(goalX -v.getX()));
                //Velocidad del coche
                velX = Math.round(car.getVelocity().getX()*10f)/10f;

				velocidadMaxima = Math.max(velX, velocidadMaxima);


                //Si la velocidad en X es negativa paro el coche
                if(velX<=0){
                    car.setForce(-car.getVelocity().getX(),-car.getVelocity().getY());    
                } else {
                    //El coche esta en movimiento y no va hacia atras y esta en la recta
                    if(car.getEnergy()>=1 && car.getVelocity().getX()>=0 && v.getY()>=375f){
                        car.setForce(-controller.step(velX, dist),0);
                     }
                }


				if ((velX<=0 && v.getY()>= 300) || v.getY()>= 390)
				{
					if (iteraciones++ < maxIteraciones)
					{
						System.out.println((iteraciones - 1) + " " + (goalX-v.getX()));
						System.out.println("Velocidad máxima: " + velocidadMaxima);
						needsReset = true;
					}
				}


                //System.out.println(car.getPosition());
	}
}
