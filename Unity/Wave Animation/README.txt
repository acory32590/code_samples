5/20/2015

This is some most recent personal work, exploring what can be done on a low-end mobile platform such as an older smartphone without getting too much of a performance hit.

The goal was to A) have 3D animated water and B) have objects float in the water with a visual occlusion effect.

A custom shader was made (LayerBlend) to determine what texture should be shown.  If the depth of the object was greatest, it would draw the normal texture (in my case, a wood texture).  Otherwise, the object had to be "underwater", and would thus blend its texture with a "blend texture" which was a water texture, giving a nice water look.

The WavesPerlinNoise, when applied to a large plane with a mesh, simply loops through all vertices and uses a perlin noise function to alter the height of each vertex, creating a pseudo wave motion to the plane.

Future work to make this more mobile-friendly would be to store the vertex heights in a matrix, move the plane with the player, and update the plane's vertices based off the matrix's values as well as the player's movements.  That way, the game engine would only need to render one mesh no matter how large the in-game world is, and moving around would not alter the wave motion.