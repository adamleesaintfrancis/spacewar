/**
 *
 */
package edu.ou.mlfw.ladder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ou.mlfw.config.ClientMapping;

class MappingsGenerator implements Iterable<ClientMapping[]>
{
	private static final Logger logger
		= Logger.getLogger(MappingsGenerator.class);

	//Lazily generate ClientMappings arrays from a config
	private final ClientMapping[] variableClientMappings;
	private final int clientsPerGame;
	private final ClientMapping[] allClientMappings;

	MappingsGenerator( final LadderConfig ladderconfig ) {
		variableClientMappings = ladderconfig.getVariableClientMappings();
		clientsPerGame = Math.min(ladderconfig.getMaxVariableClientsPerGame(),
								 variableClientMappings.length);

		final ClientMapping[] staticClientMappings
			= ladderconfig.getStaticClientMappings();

		allClientMappings = new ClientMapping[ clientsPerGame +
		                                       staticClientMappings.length ];

		//Copy static clients to end of allClientMappings array
		System.arraycopy( staticClientMappings, 0,
						  allClientMappings, clientsPerGame,
						  staticClientMappings.length );
	}

	public Iterator<ClientMapping[]> iterator() {
		return new Iterator<ClientMapping[]>() {
			final CombinationGenerator comboGen
				= new CombinationGenerator( variableClientMappings.length,
											clientsPerGame );
			public boolean hasNext() {
				return comboGen.hasMore();
			}

			public ClientMapping[] next() {
				final Map<String, Integer> seen = new HashMap<String, Integer>();
				int allMappingsIndex = 0;
				for(final int clientIndex : comboGen.getNext()) {
					ClientMapping clientMapping
						= variableClientMappings[clientIndex];

					//TODO:  The World always expects clients to map to
					//controllables with specific names; by changing the
					//name here, we're instituting a convention that the
					//simulator config will name controllables by appending
					//numbers to the end of a base name, while the ladder
					//config will just use the base name to indicate that the
					//clients should be drawn arbitrarily to fill those slots.
					final String name = clientMapping.getControllableName();
					Integer num_seen = seen.get(name);
					if(num_seen == null) {
						num_seen = 1;
					}
					seen.put(name, num_seen + 1);
					clientMapping = new ClientMapping(
								name + "" + num_seen,
								clientMapping.getClientInitializerFile());
					//end

					allClientMappings[allMappingsIndex++] = clientMapping;

					logger.info(
							clientMapping.getControllableName() + ": "
							+ clientMapping.getClientInitializerFile().getAbsoluteFile());
				}

				return allClientMappings;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}