#version 330 core
in vec2 UV;
out vec4 outColor;

uniform sampler2D diffuse;

void main() {
    outColor = vec4(texture(diffuse, UV).rgb, 1);
}