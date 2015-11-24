# Introduction #

This manual is under construction.  If you have any questions, comments, or suggestions, please don't hesitate to drop us an email!

# Details #

Spacewar is actually two projects:

1.)  A simple framework for writing simulators and clients for artificial intelligence algorithms, along with utilities for adding graphics, reporting on client performance, and running tournaments among many clients.

2.)  An implemented simulator, Spacewar, that allows students to take control of a small spaceship navigating around moving asteroids and other hostile ships.  The controller can take the form of a human using the keyboard to move the ship around, or can be code that implements an ai or machine learning algorithm.

Each component uses simple XML-based configuration to control its behavior.

# The Machine learning and AI framework #

The main class in the framework is the World class.  A World instance is responsible for driving a simulator, and for joining controllable objects exposed by the simulator with clients that control them.

# The Implemented Simulator #

The spacewar simulator is currently in use by the Introduction to Artificial Intelligence class at the University of Oklahoma.  The spacewar simulator has been used for both the Spring 2006 and Spring 2007 classes and will be used in the future.  It is general enough that our projects have included:
  * Astar clients for intelligent navigation
  * Reinforcement learning clients that learn to intelligently choose among a set of high-level actions to control each ship
  * Evolutionary computation clients that also learn to intelligently choose among a set of high-level actions
  * STRIPS-style planning agents that are able to intelligently play Capture the flag where they must coordinate actions within a multi-agent team

# Getting started #

Since the spacewar simulator package can be used both for the implemented asteroids-style gaming environment and for other simulation environments, you might wonder where to start.  The best way to start is to download the current source, build the system using the build.xml file, and run it.  The default client is a human-controlled ship that will simply sit there until you start it moving.  You can control it using the keyboard arrow and space keys.  To get started on writing your own intelligent agent, we suggest that you look at the included random and high-level random agents.  Although these agents are not intelligent at all, they give a starting point on how to construct your own agent.