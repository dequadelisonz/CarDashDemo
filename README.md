# CarDashDemo
CardashAppDemo app is a demo to show the functionality of the custom views provided by the library CardashViewLib.
These views are designed thinking of automotive applications and include:
- GaugeView: this view is meant to represent on screen values ​​such as speed, torque or engine speed
- GearWheelView: this view is meant to show the current value of a series of discrete values ​​such as the nr. ratio of current gear
- GmeterView: in devices with accelerometer this view allows you to graphically show the acceleration according to two directions. The values ​​shown assume that the device is oriented with the plane of the screen vertically (oriented toward the driver)
- HalView: loosely based on the display of the sentient computer of "2001: A Space Odyssey". This view can be used to plot the progress of an output sound / voice
- ThrottleView: this is a colored bar whose length grows from left to right as a function of the current target value assigned. Ideal to show quantities such as torque, engine speed or level of the accelerator pedal

All view can be initialized with the layout of the xml 'activity as well as its customizable parameters of each.

Browse the code of demo app and layout xml of Activity to understand how to use the view.
The layout is designed to work in landscape orientation on 7 "tablet with a resolution of 800x1280 (ie Nexus 7 2012).

The project was created with Android Studio 1.2.1.1, it is supposed to be built with Gradle.

Special thanks to Evelina Vrabie for the code of the original GaugeView:

http://android.codeandmagic.org/android-gaugeview-library/

and Yuri Kanivets for WheelView code that is used for GearWheelView:

https://code.google.com/p/android-wheel/
