# Práctica de sincronización de hilos en Java: Simulador de ascensor.

## Descripción
El proyecto consiste en un escenario en 2 dimensiones en el que he utilizado gráficos del archiconocido videojuego Super Mario Bros. El escenario está compuesto de varias plataformas dispuestas verticalmente y separadas por un hueco que será por donde se irá moviendo el ascensor, en este caso representado por una nube.

![Screenshot](https://cloud.githubusercontent.com/assets/4970177/11437915/89bbb11e-94f1-11e5-8bbb-f48af9ded935.png)

A lo largo del tiempo, en intervalos aleatorios, se irán generando personajes de forma aleatoria (Mario o Luigi). Éstos entrarán al escenario desde la parte izquierda de la pantalla en una plataforma aleatoria, siempre que ésta se encuentre libre. Los personajes se moverán de izquierda a derecha hasta llegar al hueco, donde se pararán y solicitarán el ascensor. Cuando un personaje solicita el ascensor, se encenderá el bloque que se encuentra bajo sus pies indicando el número de planta al que desea dirigirse.

El ascensor irá atendiendo a los personajes de forma ordenada conforme éstos vayan llegando y solicitándolo. El hilo del ascensor se “echará a dormir” en caso de que no haya ningún personaje solicitando sus servicios.

El funcionamiento del ascensor será: dirigirse a la planta donde se encuentra el personaje solicitante, recogerlo, desplazarlo a la planta deseada y soltarlo.

Una vez el personaje ha sido soltado en la parte derecha de las plataformas, éste continuará moviéndose hacia la derecha y podría toparse con un “premio”, los cuales también se van generando de forma aleatoria y en intervalos aleatorios. Los premios pueden consistir en:
Una seta. En cuyo caso el personaje la consumirá y adoptará su forma adulta.
Una flor. En cuyo caso el personaje la consumirá y adoptará su forma adulta de fuego.
Un enemigo, cuya representación gráfica se genera de forma aleatoria. En caso de que el personaje se encuentre con este premio negativo, el personaje morirá y caerá de la plataforma hasta desaparecer de la pantalla. Su viaje habrá finalizado.

Si todo ha ido bien, en este punto el personaje continuará moviéndose hacia la derecha hasta entrar en la tubería y salir por el extremo inferior derecho de ésta, hasta que finalmente el personaje abandone la pantalla y concluya su trayecto.

## Sincronización

En este proyecto, cada personaje representa un hilo de ejecución diferente y tendrán que sincronizarse con el ascensor que casualmente es otro hilo de ejecución.

Antes de continuar es necesario aclarar que para garantizar que el ascensor va atendiendo a los personajes de forma ordenada conforme van solicitándolo he utilizado una estructura de datos de tipo LinkedList que utilizo como cola de personajes con prioridad FIFO, para ello únicamente hay que utilizar el método LinkedList#removeFirst().

Los hilos se sincronizarán al llegar al hueco de la plataforma, momento en el cual invocarán el método sincronizado Ascensor#solicitar() que podemos observar a continuación:

![Screenshot](https://cloud.githubusercontent.com/assets/4970177/11437909/89a1deb0-94f1-11e5-9e2d-2847d33e9699.png)

Lo primero será introducir el personaje solicitante en la cola de espera, y la condición para que el personaje/hilo se duerma será mientras siga estando en la cola de espera. El ascensor ya se encargará de sacar al personaje de la cola de espera, como veremos más adelante. Y cuando eso haya ocurrido, la ejecución del hilo podrá continuar y el personaje finalmente se convertirá en el siguiente en ser atendido.

La otra parte interesante de código referente a la sincronización será en el método Ascensor#run() donde se sacará un personaje de la lista de espera y se despertarán los hilos para que el proceso continúe.

![Screenshot](https://cloud.githubusercontent.com/assets/4970177/11437912/89a680f0-94f1-11e5-95c6-61e2aaac6082.png)

Durante la ejecución del hilo del ascensor se comprobará lo siguiente:
Si hay un personaje montado en el ascensor: transportarlo.
Si no hay nadie montado, pero hay un personaje que lo ha solicitado y se le está atendiendo: ir a recogerlo.
En caso de que no ocurra nada de lo anterior pueden ocurrir varias cosas:
Si la cola de espera de personajes se encuentra vacía, entonces el hilo del ascensor se echa a dormir.
En caso de haber personajes en la lista de espera, se elimina al primero de la cola y se despiertan todos los hilos.

Eliminando al primero de la cola y despertando a todos los hilos nos aseguramos de que en el método sincronizado Ascensor#solicitar() se atiende únicamente al primer personaje de la cola (que acaba de ser eliminado de ésta) y que el resto de hilos se vuelvan a echar a dormir.

Por último, es necesario comentar brevemente que en cierto punto durante la ejecución del hilo personaje será necesario despertar al ascensor justo antes de solicitarlo, puesto que podría darse el caso en el que el ascensor se encontrara previamente dormido y podría conllevar un deadlock, interbloqueo o exclusión mutua.

El snippet mostrado a continuación pertenece al método Character#run()

![Screenshot](https://cloud.githubusercontent.com/assets/4970177/11437913/89a838b4-94f1-11e5-8803-bc15786f6b98.png)

Finalmente, cuando un personaje sale de la pantalla, detengo su hilo de ejecución mediante el método Thread#currentThread()#interrupt(), lo borro del array de personajes y lo establezco a null para que el recolector de basura de Java se encargue de limpiarlo y no se llegue a producir nunca ningún memory leak.

## Jerarquía de clases

En mi proyecto he definido la clase Entity, que se trata de una clase abstracta que representa cualquier elemento “vivo” del juego, es decir, que esté situado en unas coordenadas determinadas del escenario con un tamaño específico, y sea susceptible de interactuar con otras Entities.

De la clase Entity heredarán las subclases Ascensor, Character y Prize.
A su vez, de Character heradarán las clases Mario y Luigi; y de Prize heredarán Mushroom, Flower y Enemy.

A continuación adjunto un pequeño diagrama que muestra una visión muy general de la jerarquía de clases utilizada.

![UML Diagram](https://cloud.githubusercontent.com/assets/4970177/11437914/89abff6c-94f1-11e5-9afd-e4944cbabc01.png)

Para generar objetos tanto de tipo Character como Prize he utilizado el patrón de diseño de software conocido como Factoría, que se encarga de generar personajes de forma aleatoria mediante un método estático. El siguiente snippet pertenece a CharacterFactory#newInstance().

![Snippet](https://cloud.githubusercontent.com/assets/4970177/11437910/89a5c732-94f1-11e5-8d54-dabe8fd9c760.png)

Finalmente, desde la clase principal Game se podrán obtener objetos de tipo Character mediante la factoría utilizando el método CharacterFactory#newInstance().

Lo dicho anteriormente es igualmente válido para la clase Prize y sus subclases.

Finalmente, adjunto una captura de pantalla que muestra los elementos más importantes de la aplicación:

![Screenshot](https://cloud.githubusercontent.com/assets/4970177/11437911/89a5e42e-94f1-11e5-988f-c56bfdba39c3.png)

## Licencia

        The MIT License (MIT)
        
        Copyright (c) 2015 Andrés Hernández
        
        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:
        
        The above copyright notice and this permission notice shall be included in all
        copies or substantial portions of the Software.
        
        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
        SOFTWARE.
