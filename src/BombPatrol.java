package dl;
import robocode.*;
import robocode.util.*;
import java.awt.Color;
import java.awt.geom.*;


import robocode.HitRobotEvent;
// import robocode.Robot;
import robocode.ScannedRobotEvent;

// import java.awt.*;


/**
 * bombpatrol - um robô criado por Thiago Vieira, e mantido por Thiago Vieira
 * O robô se move paralelo à borda do campo de batalha com a arma voltada para dentro.
 * @author Thiago Vieira
*/

public class BombPatrol extends AdvancedRobot {

	boolean peek; // Não se aproxime caso tenha um robô inimigo na sua mesma direção;
	double moveAmount; // Quantidade de movimentos;
	


	/**
	 * Tipo de movimento: O robô se move junto às paredes; 
	 */
	public void run() {
		// Definição das cores
		setBodyColor(new Color(130, 157, 54));
		setGunColor(new Color(214, 52, 32));
		setRadarColor(new Color(252, 175, 44));
		setBulletColor(new Color(252, 175, 44));
		setScanColor(new Color(255, 227, 2));


		// Inicializa com a quantidade máxima possível de movimentos para esse campo de batalha. O robô caminha em todas as partes da arena.
		moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
		// Inicializar a espiadinha(scan).
		peek = false;		



		// Vire à esquerda e  vá em direção a parede.
		turnLeft(getHeading() % 90);
		ahead(moveAmount);
		// Vire o canhão à direita 90º.
		peek = true;
		turnGunRight(90);
		turnRight(90);

		while (true) {
			// Dê uma espiadinha(scan) antes de virar até chegar na próxima parede.
			peek = true;
			// Mova-se em direção à parede.
			ahead(moveAmount);
			peek = false;
			// Vire-se em direção à próxima parede em 120º.
			turnRight(120);
		}
	}


/* Esse método executado quando o seu robot bate em outro robot
Aproveite que está bem perto de um inimigo e vire o 
canhão para ele e mande um tiro de força máxima.
*/
	public void onHitRobot(HitRobotEvent e) {
		turnRight(e.getBearing());
		fire(10); // Tiro de Intensidade 10.
					
	}
	
public void onScannedRobot(ScannedRobotEvent e) {
		double bulletPower;// = Math.min(3.0,getEnergy());
		double myX = getX();
		double myY = getY();
		double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
		double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
		double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);
		double enemyHeading = e.getHeadingRadians();
		double enemyVelocity = e.getVelocity();
		
		if(e.getDistance() < 300) bulletPower = Math.min(3.0,getEnergy()); // Define a potência do tiro baseado na distância do inimigo.
		else bulletPower = Math.min(1.0,getEnergy());
		
		double deltaTime = 0;
		double battleFieldHeight = getBattleFieldHeight(), battleFieldWidth = getBattleFieldWidth();
		double predictedX = enemyX, predictedY = enemyY;
		while((++deltaTime) * (20.0 - 3.0 * bulletPower) < Point2D.Double.distance(myX, myY, predictedX, predictedY)){		
			predictedX += Math.sin(enemyHeading) * enemyVelocity;	
			predictedY += Math.cos(enemyHeading) * enemyVelocity;
			if(	predictedX < 17.0 
				|| predictedY < 17.0
				|| predictedX > battleFieldWidth - 17.0
				|| predictedY > battleFieldHeight - 17.0){
				predictedX = Math.min(Math.max(18.0, predictedX), battleFieldWidth - 18.0);	
				predictedY = Math.min(Math.max(18.0, predictedY), battleFieldHeight - 18.0);
				break;
			}
		}
		double theta = Utils.normalAbsoluteAngle(Math.atan2(predictedX - getX(), predictedY - getY()));
		
		setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));
		setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));

		setFire(bulletPower);		
		// O scan é chamado automaticamente quando o robô está se movendo.
		
		
	}
}	
	