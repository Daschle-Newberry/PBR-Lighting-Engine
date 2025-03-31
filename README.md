![image](https://github.com/user-attachments/assets/2487d8aa-be19-48bf-8af3-130bdca4ac52)
This project is a Physically Based Rendering (PBR) graphics engine, which uses the principles of PBR to render realistic looking materials based on the Cook-Torrence BRDF (Bidirectional Reflectance Distribution Function). 

The BRDF is used to approximate the specular and diffuse portion of the reflectance equation, which is the following: 
![CodeCogsEqn (2)](https://github.com/user-attachments/assets/f81d6f54-020f-4072-9f66-3a4b2da8de3a)


The function fr(), is the BRDF part of the reflectance eqaution. Li() is the incident radiance, and alters the outgoing radiance (Lo) depending on the light intensity and color. The dot product of the normal (n) and the incident light direction (wi) scales the outgoing radiance depending on if the point (or fragment in our case) is facing the light source.


**Let's look at the BRDF and how it is calculated. **

PBR works based on the core principle that a surface is made up of microfacets, which are very small reflective surfaces. Depending on the roughness of the surface, these microfacets will be aligned in different directions, which changes how the surface interacts with light.

For our cases we will break down the BRDF into two parts Kd (diffuse light) and Ks (specular light).

The Ks term is calculated by the following equation:


