package edu.ou.mlfw.gui;

import java.util.*;

import edu.ou.mlfw.Client;

/**
 * TODO: Temporary class that prevents having to change the client api for
 * working with shadows.  This should eventually be replaced by simply having
 * the client track their own shadows, then providing an iterable that
 * traverses the clients directly.
 *
 * This class adapts a list of clients to act as an iterable over the
 * shadows provided by those clients, handling the persistence of shadows from
 * timestep to timestep (this was previously handled directly by the gui
 * component).
 *
 * @author Jason
 */
public class ClientShadowSource implements Iterable<Shadow2D> {
	private final Map<Client, Set<Shadow2D>> shadows
		= new HashMap<Client, Set<Shadow2D>>();

	public void update(final Iterable<Client> clients) {
		for(final Client c: clients) {
			if (c instanceof Drawer) {
				final Drawer d = (Drawer)c;
				Set<Shadow2D> shadowset = shadows.get(d);
				if(shadowset == null) {
					shadowset = new HashSet<Shadow2D>();
				}

				final Set<Shadow2D> toregister = d.registerShadows();
				if (toregister != null) {
					shadowset.addAll(toregister);
				}

				final Set<Shadow2D> tounregister = d.unregisterShadows();
				if (tounregister != null) {
					shadowset.removeAll(tounregister);
				}
			}
		}
	}

	public Iterator<Shadow2D> iterator() {
		return new Iterator<Shadow2D>() {
			private final Iterator<Set<Shadow2D>> topiter
				= shadows.values().iterator();
			private Iterator<Shadow2D> bottomiter;
			public boolean hasNext() {
				if((bottomiter == null) || !bottomiter.hasNext()) {
					if(topiter.hasNext()) {
						bottomiter = topiter.next().iterator();
					}
				}
				return (bottomiter != null) && bottomiter.hasNext();
			}
			public Shadow2D next() {
				if(hasNext()) {
					return bottomiter.next();
				}
				throw new NoSuchElementException();
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
