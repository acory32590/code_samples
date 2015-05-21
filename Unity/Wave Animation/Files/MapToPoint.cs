using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class MapToPoint : MonoBehaviour {

	public float snapBackSpeed = 5.0f;

	int vertexToSnap = -1;
	Transform water;
	Mesh mesh;
	Vector3[] vertices;

	void OnCollisionEnter(Collision collision) {
		water = collision.transform;
		if (this.rigidbody == null)
			return; // don't do anything if this object doesn't have a rigidbody

		mesh = water.GetComponent<MeshFilter> ().mesh;
		vertices = mesh.vertices;

		Vector3 point = this.transform.position;
		vertexToSnap =  NearestVertexTo (point);

		Destroy(this.rigidbody); // now remove our rigidbody component
	}

	void Update() {
		// Don't snap until we have a value to snap to
		if (vertexToSnap == -1)
			return;

		// Interpolate object to vertex position
		this.transform.position = Vector3.Lerp(this.transform.position, mesh.vertices[vertexToSnap], snapBackSpeed*Time.deltaTime);
		//obj.position = mesh.vertices[vertexToSnap];
	}

	public int NearestVertexTo(Vector3 point) {
		float minDistanceSqr = Mathf.Infinity;
		int nearestVertex = -1;
		
		// scan all vertices to find nearest
		for (int i=0;i<vertices.Length;i++)
		{
			Vector3 vertex = vertices[i];
			//Vector3 diff = point-vertex;
			Vector2 diff = new Vector2(point.x, point.z) - new Vector2(vertex.x, vertex.z);
			float distSqr = diff.sqrMagnitude;
			
			if (distSqr < minDistanceSqr)
			{
				minDistanceSqr = distSqr;
				nearestVertex = i;
			}
		}

		return nearestVertex;
	}
}
