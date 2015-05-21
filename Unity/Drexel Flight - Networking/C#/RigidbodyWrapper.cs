using UnityEngine;
using System.Collections;

class RigidbodyWrapper : Wrapper {

	public float rollAmpFactor = 2.0f;
	private Vector3 targetVelocity;
	private float targetPitch;
	private float targetRoll;
	//private float velocityAdjustWeight = 0;
	
	public float smoothSpeed = 1.0f;
	
	void Update () {
		if(networkView.isMine) {
			checkKill();
			targetVelocity = transform.InverseTransformDirection(rigidbody.velocity);
			Packet p = simClient.packet;
			float t = Time.deltaTime * smoothSpeed;
			//Debug.Log(t);
			p.xLinVel = Mathf.Lerp((float)p.xLinVel, targetVelocity.z, t);
			p.yLinVel = Mathf.Lerp((float)p.yLinVel, targetVelocity.y, t);
			p.zLinVel = Mathf.Lerp((float)p.zLinVel, targetVelocity.x, t);
			
			Vector3 rotation = transform.rotation.eulerAngles;
			float pitch = rotation.x > 180F ? rotation.x - 360F : rotation.x;
			float roll = rotation.z > 180F ? rotation.z - 360F : rotation.z;
			AssignRotation(pitch, roll);
		}
	}
}
