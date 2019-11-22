This is an individual Java project based on Genetic Algorithm. It takes in a list of sample images of the target environment and automatically search for the optimal pattern for concealment.







Miscellaneous notes


Encoding:

Gene:     Each gene encodes a trait.
    Color uses HSB system. 9 bits for H, 7 bits for S and B. 23 bits in total.
    Pattern stores the color for one point. Assuming 16 main colors maximum, 4 bits per gene.
