package edu.ou.mlfw.gui;

import java.util.*;

import edu.ou.mlfw.Client;

/**
 * TODO: Temporary class that prevents having to change the client api for
 * working with shadows. This should eventually be replaced by simply having the
 * client track their own shadows, then providing an iterable that traverses the
 * clients directly.
 * 
 * This class adapts a list of clients to act as an iterable over the shadows
 * provided by those clients, handling the persistence of shadows from timestep
 * to timestep (this was previously handled directly by the gui component).
 * 
 * @author Jason
 */
public class ClientShadowSource implements Iterable<Shadow2D> {
	private final Map<Client, Set<Shadow2D>> shadows = new HashMap<Client, Set<Shadow2D>>();

	public void update(final Iterable<Client> clients) {
		for (final Client client : clients) {
			if (client instanceof Drawer) {
				final Drawer drawer = (Drawer) client;
				Set<Shadow2D> shadowSet;
				if (shadows.containsKey(client)) {
					shadowSet = shadows.get(client);
				}
				else {
					shadowSet = new HashSet<Shadow2D>();
					shadows.put(client, shadowSet);
				}
				if (shadowSet == null) {
					shadowSet = new HashSet<Shadow2D>();
				}

				final Set<Shadow2D> toRegister = drawer.registerShadows();
				if (toRegister != null) {
					shadowSet.addAll(toRegister);
				}

				final Set<Shadow2D> toUnregister = drawer.unregisterShadows();
				if (toUnregister != null) {
					shadowSet.removeAll(toUnregister);
				}
			}
		}
		// At this point it may be necessary to traverse the 
		// shadows hashMap and remove from it any sets of shadows
		// corresponding to clients that are not included
		// in the clients Iterable passed to this function.
		// But I'm not sure what circumstance would lead to
		// there being fewer clients than there were before.
		// -BMM
	}

	public Iterator<Shadow2D> iterator() {
		return new Iterator<Shadow2D>() {

			private final Iterator<Set<Shadow2D>> topIterator = shadows
					.values().iterator();

			private Iterator<Shadow2D> bottomIterator;

			public boolean hasNext() {
				if ((bottomIterator == null) || !bottomIterator.hasNext()) {
					if (topIterator.hasNext()) {
						bottomIterator = topIterator.next().iterator();
					}
				}
				return (bottomIterator != null) && bottomIterator.hasNext();
			}

			public Shadow2D next() {
				if (hasNext()) {
					return bottomIterator.next();
				}
				throw new NoSuchElementException();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
