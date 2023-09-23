#version 330 core
layout(location = 0) in vec3 vertexPosition_modelspace;

uniform mat4 matrix;

void main() {
    gl_Position = matrix * vec4(vertexPosition_modelspace, 1);
}