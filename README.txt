An implementation that uses GeoTools quickstart [1,2,3], to analyse GPS data.

Delaunay Triangulation [4]
- Along with this tool, Java implementation of S-hull algorithm [5] for finding the Delaunay triangulation of a point set is available.

Gaberiel Graph [6]
- Gabriel Graph is constructed using the Delaunay triangulation as the input [7].

Stepping Stone Graph [8]
- A graph useful to analyze movement aspects of a data set.

Diversion Graph [9]
- A graph similar to the Stepping Stone Graph, but faster to create.

Shortest Path Graph [10]
- This graph aims to align created edges along the shortest paths taken over the given point set.


[1] https://www.geotools.org/
[2] http://docs.geotools.org/latest/userguide/tutorial/quickstart/index.html
[3] http://docs.geotools.org/latest/userguide/tutorial/quickstart/maven.html
[4] https://en.wikipedia.org/wiki/Delaunay_triangulation
[5] https://arxiv.org/abs/1604.01428
[6] https://en.wikipedia.org/wiki/Gabriel_graph
[7] https://academic.oup.com/sysbio/article-abstract/18/3/259/1624465
[8] https://dl.acm.org/doi/10.1145/3274895.3274913
[9] https://doi.org/10.4230/LIPIcs.GIScience.2021.I.7
[10] https://dl.acm.org/doi/abs/10.1145/2093973.2094010