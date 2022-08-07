<p align="right">
<a href="#ENGLISH"><b>ENGLISH</b>
<img src="https://user-images.githubusercontent.com/9969964/183218362-e981c49b-fa76-4cd1-af28-45a2be6a11f7.jpg"  style="width:15px;height:15px;">
</a>
</p>
<p align="right">
última atualização: Ago/2022
</p>


<p>
        <img width="200" src="https://user-images.githubusercontent.com/9969964/181620033-a226a6e1-84e1-427d-9eee-26a360e158d5.png" />
        <img src="http://img.shields.io/static/v1?label=STATUS&message=CONCLUIDO&color=GREEN&style=for-the-badge"/>
        <img src="https://img.shields.io/badge/OOAD-blue?style=for-the-badge"/>
       <img src="https://img.shields.io/badge/    JAVA-blue?style=for-the-badge"/>
       <img src="https://img.shields.io/badge/SPRING-green?style=for-the-badge"/>
       <img src="https://img.shields.io/badge/MONGODB-blue?style=for-the-badge"/>
       <img src="https://img.shields.io/badge/MAVEN-blue?style=for-the-badge"/>
       <img src="https://img.shields.io/badge/GIT-green?style=for-the-badge"/>
       
</p>


## _Styckers App : exercitando análise e design orientados ao objeto_
O Styckers App foi um projeto de imersão backend com a linguagem Java, proposto pela escola [alura](https://www.alura.com.br) de tecnologia. A proposta visa criar um app que irá consumir dados de uma API que retorna a classificação de filmes e séries, ao fim do projeto pretende criar stickers personalizado com indicação de filmes/séries para enviar por whatsapp


#### Motivação do exercício de análise
Nas 2 primeiras aulas da imersão, o [parseador de arquivos json](https://github.com/alematema/styckers/blob/master/test/edu/undra/styckers/util/JsonParser.java) (implementado usando regex), apresentou-se limitado a extrair pares atributo-valor de apenas jsons pouco aninhados  ([ver <b>teste de insuficiência da regex</b>](https://github.com/alematema/styckers/blob/master/test/edu/undra/styckers/util/RegexInsuficiencyTest.java)). Para se processar jsons com aninhamentos mais fundos, seria necessário ser escrita uma outra <b>regex</b> ([ver <b>teste de insuficiência da regex</b>](https://github.com/alematema/styckers/blob/master/test/edu/undra/styckers/util/RegexInsuficiencyTest.java)). Ainda, para outros jsons, seria necessário ser escrita uma terceira regex. Essa limitação nos pareceu um potencial de criação de complexidade no código do projeto.<br> 
Além dessa limitação, apresentou-se problema de se ter que escrever uma interface diferente para cada número de pares atributo-valor. Novamente, esse problema é um potencial de criação de complexidade na forma de vários pontos de manutenção no código.<br> 
<br>A <b>solução</b> que criamos resolveu essas 2 limitações <b>usando encapsulamento</b>. 

#### Resumo solução que criamos
  
A classe [ContentExtractor](https://github.com/alematema/styckers/blob/master/src/edu/undra/styckers/util/ContentExtractor.java) implementa um <b>algoritmo extrator genérico</b> de pares atributo-valor ([ver <b>testes de profundidade de aninhamento</b> de jsons](https://github.com/alematema/styckers/blob/master/test/edu/undra/styckers/util/ContentExtractorTest.java)) . Esse algorítmo que criamos combina <b>State Design Pattern</b> e um pouco de <b>recursão</b>; o algoritmo <b>atravessa</b> um arquivo json e <b>processa adequamente cada caractere</b> e simultaneamente vai computando o conjunto de pares atributo-valor.
 
  
A classe [Content](https://github.com/alematema/styckers/blob/master/src/edu/undra/styckers/util/Content.java) <b>encapsula a variação do número de pares atributo-valor</b>. Armazenamos os pares atributo-valor numa instância de um HashMap Java e guardamos uma referência para esse hashmap na classe Content. Assiim, temos uma <b>única maneira uniforme de se ler qualquer que seja o atributo e qualquer que seja o número</b> de pares atributo-valor:<b>  content.get("atributo") </b>.
  
  
A classe [SourceApi](https://github.com/alematema/styckers/blob/master/src/edu/undra/styckers/SourceApi.java) é um java enumeration de lambda expressions que  <b>encapsula a variação dos algoritmos que lidam com especificidade de cada API</b>.
  
  
  Como se vê no <b>uml abaixo</b>, o [StyckerApp](https://github.com/alematema/styckers/blob/master/src/edu/undra/styckers/StyckerApp.java) <b>está totalmente imune às mudanças</b> de uma API para outra API, pois é no [SourceApi](https://github.com/alematema/styckers/blob/master/src/edu/undra/styckers/SourceApi.java) que é feito o setting do algoritmo específico <b>( implementado como uma λ expression )</b> que o StyckerApp irá invocar.<br> 
O algoritmo especifico <b>( implementado como uma λ expression )</b> recupera o json de uma API usando, indiretamente, a classe [Http](https://github.com/alematema/styckers/blob/master/src/edu/undra/styckers/util/Http.java); <br>
Em sequida, ele invoca, indiretamente,  o [ContentExtractor](https://github.com/alematema/styckers/blob/master/src/edu/undra/styckers/util/ContentExtractor.java), passando ao ContentExtractor <b>as keys e o json que lhe interessam</b>; o ContentExtractor retorna o key-value pairs set (encapsulado em instâncias de [Content](https://github.com/alematema/styckers/blob/master/src/edu/undra/styckers/util/Content.java)); e então, o algoritmo processa esses key-value pairs como ele desejar e, por fim, o algorítmo invoca, indiretamente, o [StyckerGenerator](https://github.com/alematema/styckers/blob/master/src/edu/undra/styckers/StyckerGenerator.java) e o resultado acontece. 
  
#### UML

![uml-styckers-app](https://user-images.githubusercontent.com/9969964/183126255-c1c36789-4a68-4e16-b7a1-a35d179697c5.png)


#### Análise e Design OO do Stycker App        
O texto abaixo detalha nossa análise e design final da app que implementamos.
        
![Description-1](https://user-images.githubusercontent.com/9969964/181584362-8840a1d0-8b9e-4f6d-b6c0-786373f9bc25.png)
![Description-2](https://user-images.githubusercontent.com/9969964/181584396-bd75b541-b274-46f1-8643-a41a66da1811.png)
![Description-3](https://user-images.githubusercontent.com/9969964/181584445-83bcb112-a9b1-4348-be67-703f0de80144.png)
#### Conclusão
<b>Aplicamos sucessivamente o princípio de encapsulamento</b>, e então o código do projeto ficou organizado em duas partes: <br><br>
        <b>a parte que não varia, o StyckerApp</b><br>
        <b>a parte que varia de API para API, ou de InputStream para InputStream, ou de uma String para outra String.</b><br><br>
O <b>StyckerApp é totalmente fechado para mudança</b>, é <b>totalmente imune à mudança</b> : não precisamos mudar nada nele quando queremos chamar outra API diferente de uma API atual <b>(basta ser escrita uma nova λ expression para recupear o json da API, ou de qualquer outra fonte)</b>. <br>
<b>Não precisamos mudar nada no StyckerApp</b> quando se necessitar gerar styckers apartir de algum json que está em um InputStream qualquer.<br>
<b>Não precisamos mudar nada no StyckerApp</b> quando queremos 5 valores de 5 chaves ou quando queremos 1 valor de 1 chave ou quando queremos 1.000.000 de valores de 1.000.000 de chaves... isto é assim porque o método <b>getContents</b> foi implementado usando <b>Varargs</b> de String e porque <b>LEMOS</b>, de um objeto [Content](https://github.com/alematema/styckers/blob/master/src/edu/undra/styckers/util/Content.java), qualquer valor, <b>de uma maneira uniforme</b> : <b> content.get("key") </b>. <br><br>
Para cada um dos exemplos de variação <b>( que não afeta o código do StyckerApp )</b> acima, escrevemos uma λ expression( um algorítmo ) que sabe lidar com essa especificidade e essa λ expression invoca os métodos adequados do StyckerApp. <br><br>
Por causa deste <b>design muito bem encapsulado, é fácil e agradável dar manutenção nesse código e muito fácil de ampliar o StyckerApp</b>.

<br><br><br><br><br><br><br><br><br><br>
### Alguns styckers gerados

![JAVA](https://user-images.githubusercontent.com/9969964/183130804-1bc0e5dd-8c32-45cc-bde0-d0985dcf3e87.png)
![C](https://user-images.githubusercontent.com/9969964/183130896-9f5c32d4-028b-4939-880f-d9d1bab7eb9b.png)
![JAVASCRIPT](https://user-images.githubusercontent.com/9969964/183131171-94e6c158-2db2-47cb-8620-034a7cc8607e.png)

<br><br><br><br><br><br><br><br><br><br><br><br><br>
### Tecnologias 
<p>
    <img src="https://img.shields.io/badge/OOAD-green?style=for-the-badge"/>
    <img src="https://img.shields.io/badge/JAVA-blue?style=for-the-badge"/>
    <img src="https://img.shields.io/badge/SPRING-green?style=for-the-badge"/>
    <img src="https://img.shields.io/badge/MONGODB-blue?style=for-the-badge"/>
    <img src="https://img.shields.io/badge/MAVEN-blue?style=for-the-badge"/>
    <img src="https://img.shields.io/badge/GIT-green?style=for-the-badge"/>
    <img src="https://img.shields.io/badge/GIT HUB-pink?style=for-the-badge"/>
</p>

### Suportes 
<p>
    <img src="https://img.shields.io/badge/JSON-lightgreen?style=for-the-badge"/>
    <img src="https://img.shields.io/badge/HTML-inactive?style=for-the-badge"/>
    <img src="https://img.shields.io/badge/CSV-inactive?style=for-the-badge"/>
    <img src="https://img.shields.io/badge/XML-inactive?style=for-the-badge"/>
   
</p>


<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
#### ENGLISH

<p align="right">
<a href="#readme"><b>PORTUGUÊS</b>
<img src="https://user-images.githubusercontent.com/9969964/183219113-bdc5a0d2-5249-404a-8dc1-bf0e9c9f7b28.jpg"  style="width:15px;height:15px;">
</a>
</p>
