# Jmorph

This project was completed my final semester of college. The pdf of the prompt I recieved is titled "exercise-10.pdf"

This was written and tested on MacOS and MorphUtil.java / ControlPointsFrame.java may need to be revised depending on the machine it is ran on. 
Specifically lines 90, 98, and 105 on MorphUtil and line 74 on ControlPointsFrame (path dependencies)

You will need to create a new directory in src titled "morph"

After morph is complete and the between frames have been generated, navigate to src/morph and run the following in the terminal to generate morphing animation in .mp4 format:

ffmpeg -r 30 -f image2 -s 500x500 -i %d.jpeg -vcodec libx264 -crf 25  -pix_fmt yuv420p test.mp4

Will need to install ffmpeg if not already done so

