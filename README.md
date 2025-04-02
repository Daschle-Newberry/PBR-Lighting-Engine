![image](https://github.com/user-attachments/assets/2487d8aa-be19-48bf-8af3-130bdca4ac52)
This project is a Physically Based Rendering (PBR) graphics engine, which uses the principles of PBR to render realistic looking materials based on the Cook-Torrence BRDF (Bidirectional Reflectance Distribution Function). 

The BRDF is used to approximate the specular and diffuse portion of the reflectance equation, which is the following: 
![reflectanceequation](https://github.com/user-attachments/assets/bfdfce54-9964-46cc-91cd-63f23084aa3e)


The function fr(), is the BRDF part of the reflectance eqaution. Li() is the incident radiance, and alters the outgoing radiance (Lo) depending on the light intensity and color. The dot product of the normal (n) and the incident light direction (wi) scales the outgoing radiance depending on if the point (or fragment in our case) is facing the light source.


**Let's look at the BRDF and how it is calculated. **

PBR works based on the core principle that a surface is made up of microfacets, which are very small reflective surfaces. Depending on the roughness of the surface, these microfacets will be aligned in different directions, which changes how the surface interacts with light.

For our cases we will break down the BRDF into two parts Kd (diffuse light) and Ks (specular light).

The Ks term is calculated by the following equation: <br/>
<img  src = "https://github.com/user-attachments/assets/0a21ffb4-076a-43d7-9670-40fdd7585f11" width = 500;x/>

The Normal Distribution Function (D):
![NDF](https://github.com/user-attachments/assets/146f2fcc-be42-430c-85ca-c1997485158f)
The Normal Distribution Function (NDF) represents the amount of surface area that is aligned with the half-vector between the incident light direction and the view direction. Using the surface normal, the half-vector, and the roughness paramter (alpha) we can calculate the D term. 

The Geometry Function (G):
![GeometryFunction](https://github.com/user-attachments/assets/35ad338f-1b4d-4e61-8cee-c5360fff71d9)

Similarily to the NDF, the Geometry function represents the amount of surface area that is self shadowed. Since microfacets can either obstruct light from reaching the viewer or reflect the light away from the viewer, we must calculate the geometry term once using the light vector and once using the view vector:
![geometry](https://github.com/user-attachments/assets/686105c7-72d9-4592-9572-3e4b8507d53f)



