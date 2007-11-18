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
	private final int agentsPerGame;
	private final ClientMapping[] allClientMappings;
	
	MappingsGenerator( final LadderConfig ladderconfig ) {
		variableClientMappings = ladderconfig.getVariableClientMappings();
		agentsPerGame = Math.min(ladderconfig.getMaxVariableAgentsPerGame(),
								 variableClientMappings.length);
		
		final ClientMapping[] staticClientMappings 
			= ladderconfig.getStaticClientMappings();

		allClientMappings = new ClientMapping[ agentsPerGame + 
		                                       staticClientMappings.length ];

		//Copy static clients to end of allClientMappings array
		System.arraycopy( staticClientMappings, 0,   
						  allClientMappings, agentsPerGame, 
						  staticClientMappings.length );
	}

	public Iterator<ClientMapping[]> iterator() {
		return new Iterator<ClientMapping[]>() {
			final CombinationGenerator comboGen 
				= new CombinationGenerator( variableClientMappings.length, 
											agentsPerGame );
			public boolean hasNext() {
				return comboGen.hasMore();
			}
			
			public ClientMapping[] next() {
				int allMappingsIndex = 0;
				for(int clientIndex : comboGen.getNext()) {
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