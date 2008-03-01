/**
 *
 */
package edu.ou.mlfw.ladder;

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
				int allMappingsIndex = 0;
				for(final int clientIndex : comboGen.getNext()) {
					final ClientMapping clientMapping
						= variableClientMappings[clientIndex];

					allClientMappings[allMappingsIndex++] = clientMapping;

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