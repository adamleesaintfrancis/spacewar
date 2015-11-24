# FAQ #

## Why did you create spacewar? ##

Spacewar is designed as a educational tool for introductory artificial intelligence classes.  It is designed to be easy to create new agents and to be extensible to the many kinds of projects an AI class may do.

## How do I run it? ##

Sychronize with the current subversion repository, and use the provided ant build to start up the program right away.  Details on setting up Spacewar for class use will be made available shortly; if you need to get up and running with that right away, email us!  If you're the first to approach us, you will probably recognize the future documentation on the process from that email thread. ;)

## Should I wait for a code update before trying to use the program? ##

Not at all, dive right in!  We are not currently using point releases; instead you can use the subversion repository to keep in sync with the latest changes.  We do try to make sure that we don't check in broken code; now that we've released to the public, backwards imcompatible changes will be rare, and will be marked with point releases.  The process will stabilize as we adjust to serving a community rather than just our own needs.

## What are some of the plans for the future? ##

  1. Documentation, documentation, documentation.
  1. We are in the process of finishing up a rewrite of large parts of the code base, that address many lessons learned from the first year this was used in class.  These changes focus on making the code cleaner and easier to understand, and the apis minimal and clear. These changes continue, and we are happy to consider feedback!
  1. The team system is being completely overhauled, and is not currently in the codebase.  The design has been finalized, and the implementation will be in place very soon (as we are about to assign the team project!)
  1. At the moment, we do not mark point releases, and are developing out of subversion.  This will change as we settle into maintenance mode!
  1. Right now, statistics and reporting is done in a fairly ad hoc manner.  We're working on a way to make this more accessible, and to expose custom stats tracking for clients.
  1. Good practices: logging, regression and unit testing, javadocs.
  1. Parallelization, and maybe distributed processing; these are fairly low priority at this point.





