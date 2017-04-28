# Installation Guide
This installation guide will lead you throughout the installation process for various operating systems.

## Windows
* Check if you have the latest Java Runtime Environment. If you do, skip the next two steps.

* Download the latest Java Runtime Environment from <http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html>.

* Run the setup.

* Download the latest Astrarium release from <https://github.com/SirPryderi/Astrarium/releases/latest>.
 
* Run the application by double clicking the `*.jar` file.


## Linux
### Debian/Ubuntu

Original guide from [DigitalOcean](https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-debian-8/).

* Get administrative rights.

        sudo su 

* Update the repositories.

        apt-get update 

* Install a utility to add a new repository.

        apt-get install software-properties-common
        
* Add the Oracle Java 8 repository.

        add-apt-repository "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main"

* Update the sources again.

        apt-get update
        
* Install the official Oracle Java 8 JRE.

        apt-get install oracle-java8-installer
        
* Test if everything worked with:
        
        javac -v
        
    that should return something like `javac 1.8.0_111`.
    
* Exit from the root session.

        exit
    
* Download the latest Astrarium release from <https://github.com/SirPryderi/Astrarium/releases/latest>.

* Run it with:

        javac -jar Astrarium.jar
        
* Another happy Linux user!