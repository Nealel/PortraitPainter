# PortraitPainter
PortraitPainter is a generative art program, capable of creating digital portraits in a variety of styles, without using references images or hand-crafted construction grammars.


How does it work?
------
PortraitPainter uses a combination of genetic algorithms and face detection techniques to evolve face-like images over time.
- **Evolutionary Algorithms**: Evolutionary algorithms are used to explore the space of possible images and converge on the best ones.
- **Face Detection**: This program uses a modified version of the Viola Jones face detection algorithm implemented at [JViolaJones](https://github.com/tc/jviolajones)
- **Images**: Images are represented as lists of shapes, each with co-ordinates, colour, and opacity. This gives the images their distinctive style, as well as being easier for the GA to select for than pixel-level details.

How do I use it?
------
This project is available as a ready-to-run Jar file under the [releases section](https://github.com/Nealel/PortraitPainter/releases). 

Once running,the system allows a number of settings to be customized. These are split into three groups: GA (genetic algorithm) settings, Image settings, and Colour palette. The last two groups control the style of the image, and experimentation with these settings is highly encouraged. 
However, blind experimentation with the GA settings may stop the evolutionary system from working. These are set to roughly appropriate values, but may need to be configured differently for certain image settings. A good rule of thumb for these settings is 'if it's not broken, don't fix it'.
Individual settings are explaining within the program. 

Depending on the settings, it may take anywhere from fifteen minutes to an hour to produce the final face. However, at any point, images can be saved and the process can be terminated.

Contributors
-----
This project was created by [@NealeL](https://github.com/Nealel) in partial fulfilment of a Master's degree. It uses a modified version of the [JViolaJones](https://github.com/tc/jviolajones), and uses the haarcascade_frontalface_default detector from the OpenCV library.
