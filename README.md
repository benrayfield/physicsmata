# physicsmata
API for all possible cellular automata that work same at all angles 

Its strange how the "sorted pointers" normalizing makes just about any random function, as long as it connects the inputs to the outputs on some path, vibrate as some nonlinear shape of wave. This could be used as a game interface for evolvable musical instruments or fluid puzzle games. Physicsmata is similar in effect to SmoothLife but simpler and pure Java. The cellular automata API takes a function to run at each point. Its parameters are sums of screen brightness (n color dimensions are optional) at concentric circles around each point, efficiently costing only the perimeter of those circles (instead of the area) because of the tree of squares. You can create any function, or to get started try the visual function editor where each click changes if a column is sine/sigmoid/half/double/exp/etc or which of the 0, 1, or 2 columns to the left are its params. Every click changes the patterns of waves seen on the left, which run that function you visually built at each point.
