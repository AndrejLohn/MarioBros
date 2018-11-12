package com.andrejlohn.mariobros.tools;

import com.andrejlohn.mariobros.sprites.InteractiveTileObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * This class represents a generic listener to notice the collision between two game world objects.
 *
 * @version %I%, %G%
 * @see     ContactListener
 */
public class WorldContactListener implements ContactListener {

    /**
     * Reacts to contact initialisation between two game world objects.
     *
     * @param contact   the contact information
     * @see Contact#getFixtureA()
     * @see Contact#getFixtureB()
     * @see Fixture#getUserData()
     */
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if("head".equals(fixA.getUserData()) || "head".equals(fixB.getUserData())) {
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if(object.getUserData() instanceof InteractiveTileObject) {
                ((InteractiveTileObject) object.getUserData()).onHeadHit();
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
