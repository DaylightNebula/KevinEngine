#version 330 core

uniform vec4 color;
uniform vec4 borderColor;
uniform vec4 border; // top bottom left right
in vec2 UV;
out vec4 outColor;

void main() {
    if (UV.y < border.x || UV.x < border.z || 1 - UV.y < border.y || 1 - UV.x < border.w) {
        outColor = borderColor;
        return;
    }

    outColor = vec4(UV, 0, 1);
}