/** Ben F Rayfield offers HumanAiCore opensource GNU LGPL, including WeightsNode:
<br><br>
Nodes that have a few vars as state which is updated based on reading state of
other nodes such as by a weightedSum or any way of reading the weights and their states.
Its important that no node modify any other nodes, so it can be done in parallel
and the weightFrom other nodes all be stored locally for cache reasons.
That means edge types that require symmetric weights must still store both.
<br><br>
Setting a weight to anything other than 0 connects the nodes automatically,
and setting a weight to 0 deletes that connection.
<br><br>
Its a very general kind of node that I plan to use to unify:
PhysicsmataV2.0.0 (wave based cellular automata),
SparseDoppler,
BlackHoleCortex (boltzmann neuralnet, in humanainet so GPL),
BayesianCortex (GPL),
and various other code.
You may find it useful as a general datastruct and way of flowing scalars.
*/
package humanaicore.weightsnode;