package PBREngine.renderer.buffers;

import org.joml.Vector2f;

public interface Sampler2D {

    void bindTo(int binding);
    void bind();
    int getTextureID();
    Vector2f getDimensions();}
