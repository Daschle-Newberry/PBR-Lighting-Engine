![image](https://github.com/user-attachments/assets/2487d8aa-be19-48bf-8af3-130bdca4ac52)
This project is a Physically Based Rendering (PBR) graphics engine, which uses the principles of PBR to render realistic looking materials based on the Cook-Torrence BRDF (Bidirectional Reflectance Distribution Function). 

<h2> How to Build </h2>
1. First clone the repository using the command below

  ```
  git clone https://github.com/Daschle-Newberry/PBR-Lighting-Engine
  ```

2. Once within the project directory, run the following command. This will create an jar file for the project that contains all the necessary resources

 ```
 gradle shadowjar
 ```

3. Locate the jar file, it should be in the build\libs folder

4. Copy the jar location and run this command in the terminal
  
  ```
  java -jar C:\Users\...\path\to\the\jar
  ```
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

<h2> Important! </h2>
This project curently has no GUI and it purely a proof of concept for a bigger project. There is a high likely hood of graphical issues and poor performance as well! These issues will be addressed in a later version of the project.

--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

<h2> Theory  </h2>
The BRDF is used to approximate the specular and diffuse portion of the reflectance equation, which is the following: <br/>

<img  src = "https://github.com/user-attachments/assets/bfdfce54-9964-46cc-91cd-63f23084aa3e" height = "50px" width = "auto"/>


The function fr(), is the BRDF part of the reflectance eqaution. Li() is the incident radiance, and alters the outgoing radiance (Lo) depending on the light intensity and color. The dot product of the normal (n) and the incident light direction (wi) scales the outgoing radiance depending on if the point (or fragment in our case) is facing the light source.


<h3> Let's look at the BRDF and how it is calculated</h3>

PBR works based on the core principle that a surface is made up of microfacets, which are very small reflective surfaces. Depending on the roughness of the surface, these microfacets will be aligned in different directions, which changes how the surface interacts with light.

For our cases we will break down the BRDF into two parts diffuse light and specular light.

<h3> The Specular portion is calculated by the following equation: </h3> <br/>

<img  src = "https://github.com/user-attachments/assets/0a21ffb4-076a-43d7-9670-40fdd7585f11" height = "50px" width = "auto"/>


<h4> The Normal Distribution Function (D): </h4> <br/>

<img  src = "https://github.com/user-attachments/assets/146f2fcc-be42-430c-85ca-c1997485158f" height = "50px" width = "auto"/>


The Normal Distribution Function (NDF) represents the amount of surface area that is aligned with the half-vector between the incident light direction and the view direction. Using the surface normal, the half-vector, and the roughness paramter (alpha) we can calculate the D term. 

<h4> The Geometry Function (G): </h4> <br/>

<img  src = "https://github.com/user-attachments/assets/35ad338f-1b4d-4e61-8cee-c5360fff71d9" height = "50px" width = "auto"/>


Similarily to the NDF, the Geometry function represents the amount of surface area that is self shadowed. Since microfacets can either obstruct light from reaching the viewer or reflect the light away from the viewer, we must calculate the geometry term once using the light vector and once using the view vector:<br/>

<img  src = "https://github.com/user-attachments/assets/686105c7-72d9-4592-9572-3e4b8507d53f" height = "25px" width = "auto"/>

<h4> Fresnel equation (F) </h4>
The fresnel equation is perhaps one of the most import parts of the BRDF, and it represents the ratio of light that is reflected over the portion that is refracted. It is calcualted using the following equation:

<img src = "https://github.com/user-attachments/assets/7794d042-0b84-4fa1-a840-836845f6ff45" height = "25px" width = "auto" />

F0 represents the base reflectivty of an object, which determines how reflective each color of a surfaces albedo is. For example, a gold reflective surface may have a F0 of (1.00, 0.71, 0.29). The fresnel equation takes the base reflectivty, and increases it by a portion of its complement. How large the compliment is is dependent on the angle between the half vector and the view vector. This calculation causes surfaces to be more reflective when they are observed at grazing angles, similar to real life. 


To finalize the specular term, we divide by 4 times the dot product of the outgoing light direction (view direction) and the normal times the dot product of the incident light direction and the normal. We do this to ensure that the outgoing light does not exceed the incoming light (law of conservation of energy).

<h3>The diffuse portion is much simpler than the specular portion and only involves two steps: </h3>

<img  src = "!https://github.com/user-attachments/assets/82ec3849-e45d-4956-8f6a-3749772aced0" height = "25px" width = "auto"/>


First we compute the ratio of light that is diffuse by first finding what portion of the light is specular (reflected). Since the fresnel term determines the magnitude of specular portion, we can use it's compliment to determine the magnitude of the specular portion:

<img src = "https://github.com/user-attachments/assets/42c451e5-30cb-4212-ac9a-f54a06515430" height = "25px" width = "auto"/>

We use the kd term to scale the color of the surface, but we must divide the surface albedo by pi. The division by pi is to ensure that the outgoing light energy is not greater than the incoming light energy as we integrate over the entire hemisphere (As seen in the BRDF):


<h3> With that, we can finally calculate the BRDF</h3>

<img src = "https://github.com/user-attachments/assets/86f8e1f9-c5e8-43f4-85d1-da58bc718d30" height = "25px" width = "auto"/>

--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

<h2> Note </h2>

While the previous calculations are used to calculate the lighting of an object with respect to a single light source, they do NOT include the image based lighting calculations, which are responsible for the majority of the specular reflections in the scene. The image based lighting is calculated using the same reflectance equation, plus some optimizations to make it possible to compute it over an entire hemisphere of infinite view angles. The primary optimization we make it precomputing the image based lighting, which then utilizes optimizations like the split-sum approximation and importance sampling

If you would like to learn more about image based lighting:
<a href = "https://learnopengl.com/PBR/IBL/Diffuse-irradiance"> Diffuse </a>
<a href = "https://learnopengl.com/PBR/IBL/Specular-IBL"> Specular </a>



