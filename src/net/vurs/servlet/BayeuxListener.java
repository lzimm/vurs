package net.vurs.servlet;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;

import org.cometd.Bayeux;

public class BayeuxListener implements ServletContextAttributeListener {

	public void attributeAdded(ServletContextAttributeEvent event) {
        if (event.getName().equals(Bayeux.ATTRIBUTE)) {
            init((Bayeux) event.getValue());
        }
    }
    
    protected void init(Bayeux bayeux) {
    }

    public void attributeRemoved(ServletContextAttributeEvent event) {
    }

    public void attributeReplaced(ServletContextAttributeEvent event) {
    }   
    
}
