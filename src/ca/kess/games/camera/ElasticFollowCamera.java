package ca.kess.games.camera;

import ca.kess.games.entities.PhysicalEntity;

/**
 * An elastic following camera is a camera which follows an entity as they move,
 * but with an elastic constant to allow for more natural looking motion.
 * 
 * @author mdkess
 *
 */
public class ElasticFollowCamera extends FollowCamera {

	public ElasticFollowCamera(PhysicalEntity target) {
		super(target);
		// TODO Auto-generated constructor stub
	}

}
