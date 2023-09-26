//#version 300 es
//layout(location = 0) in vec3 vertexPosition_modelspace;
//layout(location = 1) in vec3 vertexColor;
attribute vec3 vertexPosition_modelspace;
attribute vec3 vertexColor;

uniform mat4 matrix;
//out vec3 fragmentColor;
varying lowp vec3 fragmentColor;

void main() {
    gl_Position = matrix * vec4(vertexPosition_modelspace, 1);
    fragmentColor = vertexColor;
}