/**
 *
 */
package edu.ou.mlfw.ladder;

import java.util.HashSet;
import java.util.Iterator;

import edu.ou.mlfw.config.ClientMapping;

class MappingsGenerator implements Iterable<ClientMapping[]>
{
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
				HashSet<String> seen = new HashSet<String>();
				int allMappingsIndex = 0;
				for(final int clientIndex : comboGen.getNext()) {
					ClientMapping clientMapping	= variableClientMappings[clientIndex];
					
					if (seen.contains(clientMapping.getControllableName())) {
						int append = 1;
						ClientMapping temp;
						do {
							temp = new ClientMapping(clientMapping.getControllableName() + " " + append, clientMapping.getClientInitializerFile());
							append++;
						} while (seen.contains(temp.getControllableName()));
						
						clientMapping = temp;
					}

					allClientMappings[allMappingsIndex++] = clientMapping;

					seen.add(clientMapping.getControllableName());
					
					LadderServer.logger.info(
							clientMapping.getControllableName() + ": "
							+ clientMapping.getClientInitializerFile());
				}

				return allClientMappings;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}